package com.wmenjoy.utils.lang;

import java.util.regex.Pattern;

public enum RegexConstants {
	//手机号的正则表达式
	MOBILE("(\\+?86)?1\\d{10}", 11, 14) {
		@Override
		public String regular(String param) {
			if(this.isValid(param)){
				return param.substring(param.length() - 11, param.length());
			}
			return null;
		}
	},
	;
	//缓存，加速
	private final Pattern pattern;
	//最小长度
	private final int minLength;
	//最大长度
	private final int maxLenth;
	//是否可以为空
	private final boolean allowedBlank;
	private RegexConstants(String regex, int minLength, int maxLenth) {
		this.pattern = Pattern.compile(regex);
		this.minLength = minLength < 0 ? 0 : minLength;
		this.maxLenth = maxLenth > this.minLength ? maxLenth : this.minLength;
		
		if(this.minLength == 0){
			allowedBlank = this.match("");
		} else {
			allowedBlank = false;
		}
	}
	public Pattern getPattern() {
		return pattern;
	}
	public int getMinLength() {
		return minLength;
	}
	public int getMaxLenth() {
		return maxLenth;
	}
	
	public boolean isValid(final String param){
		if(StringUtils.isBlank(param)){
			return this.allowedBlank;
		}
		
		if(param.length() < this.minLength || param.length() > this.maxLenth){
			return false;
		}
		
		return match(param);
	}
	//标准化参数
	public String regular(String param){
		return param;
	}
	
	private boolean match(final String param){
		return this.pattern.matcher(param).matches();
	}
	
}
