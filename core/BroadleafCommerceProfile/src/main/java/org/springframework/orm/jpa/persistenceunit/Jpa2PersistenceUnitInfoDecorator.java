package org.springframework.orm.jpa.persistenceunit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.persistence.spi.PersistenceUnitInfo;

import org.springframework.util.ClassUtils;

/**
 * Decorator that exposes a JPA 2.0 compliant PersistenceUnitInfo interface for a
 * JPA 1.0 based SpringPersistenceUnitInfo object, adapting the <code>getSharedCacheMode</code>
 * and <code>getValidationMode</code> methods from String names to enum return values.
 */
public class Jpa2PersistenceUnitInfoDecorator implements InvocationHandler {

	private final PersistenceUnitInfo target;

	private final Class<? extends Enum> sharedCacheModeEnum;

	private final Class<? extends Enum> validationModeEnum;

	@SuppressWarnings("unchecked")
	public Jpa2PersistenceUnitInfoDecorator(PersistenceUnitInfo target) {
		this.target = target;
		try {
			this.sharedCacheModeEnum = (Class<? extends Enum>)
					ClassUtils.forName("javax.persistence.SharedCacheMode", PersistenceUnitInfo.class.getClassLoader());
			this.validationModeEnum = (Class<? extends Enum>)
					ClassUtils.forName("javax.persistence.ValidationMode", PersistenceUnitInfo.class.getClassLoader());
		}
		catch (Exception ex) {
			throw new IllegalStateException("JPA 2.0 API enum types not present", ex);
		}
	}

	public final PersistenceUnitInfo getTarget() {
		return this.target;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("getSharedCacheMode")) {
			return Enum.valueOf(this.sharedCacheModeEnum, (String) this.target.getClass().getMethod("getSharedCacheModeName", new Class[]{}).invoke(this.target, new Object[]{}));
		}
		else if (method.getName().equals("getValidationMode")) {
			return Enum.valueOf(this.validationModeEnum, (String) this.target.getClass().getMethod("getValidationModeName", new Class[]{}).invoke(this.target, new Object[]{}));
		}
		else {
			return method.invoke(this.target, args);
		}
	}
}
