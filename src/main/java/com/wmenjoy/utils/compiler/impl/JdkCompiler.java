/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wmenjoy.utils.compiler.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.wmenjoy.utils.lang.reflect.ClassUtil;

/**
 * JdkCompiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
public class JdkCompiler extends AbstractCompiler {

	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	private final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();

	private final ClassLoaderImpl classLoader;

	private final JavaFileManagerImpl javaFileManager;

	private volatile List<String> options;

	public JdkCompiler() {
		this.options = new ArrayList<String>();
		this.options.add("-target");
		this.options.add("1.7");
		final StandardJavaFileManager manager = this.compiler
				.getStandardFileManager(this.diagnosticCollector, null, null);
		final ClassLoader loader = Thread.currentThread()
				.getContextClassLoader();
		if (loader instanceof URLClassLoader
				&& (!loader.getClass().getName()
						.equals("sun.misc.Launcher$AppClassLoader"))) {
			try {
				final URLClassLoader urlClassLoader = (URLClassLoader) loader;
				final List<File> files = new ArrayList<File>();
				for (final URL url : urlClassLoader.getURLs()) {
					files.add(new File(url.getFile()));
				}
				manager.setLocation(StandardLocation.CLASS_PATH, files);
			} catch (final IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		this.classLoader = AccessController
				.doPrivileged(new PrivilegedAction<ClassLoaderImpl>() {
					@Override
					public ClassLoaderImpl run() {
						return new ClassLoaderImpl(loader);
					}
				});
		this.javaFileManager = new JavaFileManagerImpl(manager,
				this.classLoader);
	}

	@Override
	public Class<?> doCompile(final String name, final String sourceCode)
			throws Throwable {
		final int i = name.lastIndexOf('.');
		final String packageName = i < 0 ? "" : name.substring(0, i);
		final String className = i < 0 ? name : name.substring(i + 1);
		final JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(
				className, sourceCode);
		this.javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH,
				packageName, className + ClassUtils.JAVA_EXTENSION,
				javaFileObject);
		final Boolean result = this.compiler.getTask(null,
				this.javaFileManager, this.diagnosticCollector, this.options,
				null, Arrays.asList(new JavaFileObject[] { javaFileObject }))
				.call();
		if (result == null || !result.booleanValue()) {
			throw new IllegalStateException("Compilation failed. class: "
					+ name + ", diagnostics: " + this.diagnosticCollector);
		}
		return this.classLoader.loadClass(name);
	}

	private final class ClassLoaderImpl extends ClassLoader {

		private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

		ClassLoaderImpl(final ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		Collection<JavaFileObject> files() {
			return Collections.unmodifiableCollection(this.classes.values());
		}

		@Override
		protected Class<?> findClass(final String qualifiedClassName)
				throws ClassNotFoundException {
			final JavaFileObject file = this.classes.get(qualifiedClassName);
			if (file != null) {
				final byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
				return this.defineClass(qualifiedClassName, bytes, 0,
						bytes.length);
			}
			try {
				return ClassUtil.forNameWithCallerClassLoader(
						qualifiedClassName, this.getClass());
			} catch (final ClassNotFoundException nf) {
				return super.findClass(qualifiedClassName);
			}
		}

		void add(final String qualifiedClassName, final JavaFileObject javaFile) {
			this.classes.put(qualifiedClassName, javaFile);
		}

		@Override
		protected synchronized Class<?> loadClass(final String name,
				final boolean resolve) throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}

		@Override
		public InputStream getResourceAsStream(final String name) {
			if (name.endsWith(ClassUtils.CLASS_EXTENSION)) {
				final String qualifiedClassName = name.substring(0,
						name.length() - ClassUtils.CLASS_EXTENSION.length())
						.replace('/', '.');
				final JavaFileObjectImpl file = (JavaFileObjectImpl) this.classes
						.get(qualifiedClassName);
				if (file != null) {
					return new ByteArrayInputStream(file.getByteCode());
				}
			}
			return super.getResourceAsStream(name);
		}
	}

	private static final class JavaFileObjectImpl extends SimpleJavaFileObject {

		private ByteArrayOutputStream bytecode;

		private final CharSequence source;

		public JavaFileObjectImpl(final String baseName,
				final CharSequence source) {
			super(ClassUtils.toURI(baseName + ClassUtils.JAVA_EXTENSION),
					Kind.SOURCE);
			this.source = source;
		}

		JavaFileObjectImpl(final String name, final Kind kind) {
			super(ClassUtils.toURI(name), kind);
			this.source = null;
		}

		public JavaFileObjectImpl(final URI uri, final Kind kind) {
			super(uri, kind);
			this.source = null;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
				throws UnsupportedOperationException {
			if (this.source == null) {
				throw new UnsupportedOperationException("source == null");
			}
			return this.source;
		}

		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(this.getByteCode());
		}

		@Override
		public OutputStream openOutputStream() {
			return this.bytecode = new ByteArrayOutputStream();
		}

		public byte[] getByteCode() {
			return this.bytecode.toByteArray();
		}
	}

	private static final class JavaFileManagerImpl extends
			ForwardingJavaFileManager<JavaFileManager> {

		private final ClassLoaderImpl classLoader;

		private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

		public JavaFileManagerImpl(final JavaFileManager fileManager,
				final ClassLoaderImpl classLoader) {
			super(fileManager);
			this.classLoader = classLoader;
		}

		@Override
		public FileObject getFileForInput(final Location location,
				final String packageName, final String relativeName)
				throws IOException {
			final FileObject o = this.fileObjects.get(this.uri(location,
					packageName, relativeName));
			if (o != null) {
				return o;
			}
			return super.getFileForInput(location, packageName, relativeName);
		}

		public void putFileForInput(final StandardLocation location,
				final String packageName, final String relativeName,
				final JavaFileObject file) {
			this.fileObjects.put(this.uri(location, packageName, relativeName),
					file);
		}

		private URI uri(final Location location, final String packageName,
				final String relativeName) {
			return ClassUtils.toURI(location.getName() + '/' + packageName
					+ '/' + relativeName);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(final Location location,
				final String qualifiedName, final Kind kind,
				final FileObject outputFile) throws IOException {
			final JavaFileObject file = new JavaFileObjectImpl(qualifiedName,
					kind);
			this.classLoader.add(qualifiedName, file);
			return file;
		}

		@Override
		public ClassLoader getClassLoader(
				final JavaFileManager.Location location) {
			return this.classLoader;
		}

		@Override
		public String inferBinaryName(final Location loc,
				final JavaFileObject file) {
			if (file instanceof JavaFileObjectImpl) {
				return file.getName();
			}
			return super.inferBinaryName(loc, file);
		}

		@Override
		public Iterable<JavaFileObject> list(final Location location,
				final String packageName, final Set<Kind> kinds,
				final boolean recurse) throws IOException {
			final Iterable<JavaFileObject> result = super.list(location,
					packageName, kinds, recurse);

			final ClassLoader contextClassLoader = Thread.currentThread()
					.getContextClassLoader();
			final List<URL> urlList = new ArrayList<URL>();
			final Enumeration<URL> e = contextClassLoader.getResources("com");
			while (e.hasMoreElements()) {
				urlList.add(e.nextElement());
			}

			final ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

			if (location == StandardLocation.CLASS_PATH
					&& kinds.contains(JavaFileObject.Kind.CLASS)) {
				for (final JavaFileObject file : this.fileObjects.values()) {
					if (file.getKind() == Kind.CLASS
							&& file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}

				files.addAll(this.classLoader.files());
			} else if (location == StandardLocation.SOURCE_PATH
					&& kinds.contains(JavaFileObject.Kind.SOURCE)) {
				for (final JavaFileObject file : this.fileObjects.values()) {
					if (file.getKind() == Kind.SOURCE
							&& file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}
			}

			for (final JavaFileObject file : result) {
				files.add(file);
			}

			return files;
		}
	}

}
