package com.bst.utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class NullAwareBeanUtil {
	// then use Spring BeanUtils to copy and ignore null
	public static void copyProperties(final Object src, final Object target) {
		BeanUtils.copyProperties(src, target, NullAwareBeanUtil.getNullPropertyNames(src));
	}

	private static String[] getNullPropertyNames(final Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		final java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		final Set<String> emptyNames = new HashSet<>();
		for (final java.beans.PropertyDescriptor pd : pds) {
			final Object srcValue = src.getPropertyValue(pd.getName());
			if ((srcValue == null) || (srcValue instanceof Set<?>) || (srcValue instanceof List<?>)) {
				emptyNames.add(pd.getName());
			}
		}
		final String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
}