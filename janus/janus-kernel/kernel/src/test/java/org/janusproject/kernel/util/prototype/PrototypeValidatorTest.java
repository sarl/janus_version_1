/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.kernel.util.prototype;

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PrototypeValidatorTest extends TestCase {

	private ValidatorStub validator;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.validator = new ValidatorStub();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.validator = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetAnnotationType() {
		assertSame(PrototypeStub.class, this.validator.getAnnotationType());
	}
	
	/**
	 */
	public void testGetAnnotedObjectType() {
		assertSame(Object.class, this.validator.getAnnotedObjectType());
	}
	
	private void assertInputs(Class<?> t, Object... types) {
		this.validator.validateInputs(t, types);
	}
	
	private void assertOutputs(Class<?> t, Object... types) {
		this.validator.validateOutputs(t, types);
	}

	private void assertNoInputs(Class<?> t, Object... types) {
		try {
			this.validator.validateInputs(t, types);
			fail("expecting PrototypeException"); //$NON-NLS-1$
		}
		catch(PrototypeException _) {
			// expected error was fired.
		}
	}
	
	private void assertNoOutputs(Class<?> t, Object... types) {
		try {
			this.validator.validateOutputs(t, types);
			fail("expecting PrototypeException"); //$NON-NLS-1$
		}
		catch(PrototypeException _) {
			// expected error was fired.
		}
	}
	
	private static final Double CDOUBLE = 1.0;
	private static final String CSTRING = new String();
	private static final Boolean CBOOLEAN = Boolean.TRUE;
	private static final Float CFLOAT = 1f;
	private static final Integer CINTEGER = 1;

	/**
	 */
	public void testValidate_Prototype0() {
		// No input, No output
		assertInputs(Prototype0.class);
		assertInputs(Prototype0.class, CDOUBLE, CSTRING);
		assertOutputs(Prototype0.class);
		assertOutputs(Prototype0.class, CDOUBLE, CSTRING);
	}

	/**
	 */
	public void testValidate_Prototype1() {
		// void fct()
		assertInputs(Prototype1.class);
		assertNoInputs(Prototype1.class, CDOUBLE, CSTRING);
		
		assertOutputs(Prototype1.class);
		assertNoOutputs(Prototype1.class, CDOUBLE, CSTRING);
	}
		
	/**
	 */
	public void testValidate_Prototype2() {
		// void fct(Float,String,Boolean)
		assertNoInputs(Prototype2.class);
		assertNoInputs(Prototype2.class, CDOUBLE, CSTRING);
		assertNoInputs(Prototype2.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertInputs(Prototype2.class, CFLOAT, CSTRING, CBOOLEAN);
		assertNoInputs(Prototype2.class, CFLOAT, CSTRING);
		assertNoInputs(Prototype2.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype3() {
		// void fct(Float,String[,Boolean])
		assertNoInputs(Prototype3.class);
		assertNoInputs(Prototype3.class, CDOUBLE, CSTRING);
		assertNoInputs(Prototype3.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertInputs(Prototype3.class, CFLOAT, CSTRING, CBOOLEAN);
		assertInputs(Prototype3.class, CFLOAT, CSTRING);
		assertNoInputs(Prototype3.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype4() {
		// void fct(Float...)
		assertInputs(Prototype4.class);
		assertNoInputs(Prototype4.class, CDOUBLE, CSTRING);
		assertNoInputs(Prototype4.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertNoInputs(Prototype4.class, CFLOAT, CSTRING, CBOOLEAN);
		assertNoInputs(Prototype4.class, CFLOAT, CSTRING);
		assertInputs(Prototype4.class, CFLOAT);
		assertInputs(Prototype4.class, CFLOAT, CFLOAT);
		assertInputs(Prototype4.class, CFLOAT, CFLOAT, CFLOAT);
		assertNoInputs(Prototype4.class, CFLOAT, CFLOAT, CINTEGER);
	}

	/**
	 */
	public void testValidate_Prototype5() {
		// void fct(Float,String,Boolean,Integer...)
		assertNoInputs(Prototype5.class);
		assertNoInputs(Prototype5.class, CDOUBLE, CSTRING);
		assertInputs(Prototype5.class, CFLOAT, CSTRING, CBOOLEAN);
		assertInputs(Prototype5.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertInputs(Prototype5.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CINTEGER);
		assertNoInputs(Prototype5.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CBOOLEAN);
		assertNoInputs(Prototype5.class, CFLOAT, CSTRING);
		assertNoInputs(Prototype5.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype6() {
		// void fct(Float,String[,Boolean],Integer...)
		assertNoInputs(Prototype6.class);
		assertNoInputs(Prototype6.class, Double.class, CSTRING);
		assertInputs(Prototype6.class, CFLOAT, CSTRING, CBOOLEAN);
		assertInputs(Prototype6.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertInputs(Prototype6.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CINTEGER);
		assertNoInputs(Prototype6.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CBOOLEAN);
		assertInputs(Prototype6.class, CFLOAT, CSTRING);
		assertNoInputs(Prototype6.class, CFLOAT);
		assertInputs(Prototype6.class, CFLOAT, CSTRING, CINTEGER);
		assertInputs(Prototype6.class, CFLOAT, CSTRING, CINTEGER, CINTEGER);
		assertNoInputs(Prototype6.class, CFLOAT, CSTRING, CINTEGER, CBOOLEAN);
	}

	/**
	 */
	public void testValidate_Prototype7() {
		// {Float,String,Boolean} fct()
		assertNoOutputs(Prototype7.class);
		assertNoOutputs(Prototype7.class, CDOUBLE, CSTRING);
		assertNoOutputs(Prototype7.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertOutputs(Prototype7.class, CFLOAT, CSTRING, CBOOLEAN);
		assertNoOutputs(Prototype7.class, CFLOAT, CSTRING);
		assertNoOutputs(Prototype7.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype8() {
		// {Float,String[,Boolean]} fct()
		assertNoOutputs(Prototype8.class);
		assertNoOutputs(Prototype8.class, CDOUBLE, CSTRING);
		assertNoOutputs(Prototype8.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertOutputs(Prototype8.class, CFLOAT, CSTRING, CBOOLEAN);
		assertOutputs(Prototype8.class, CFLOAT, CSTRING);
		assertNoOutputs(Prototype8.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype9() {
		// {Float...} fct()
		assertOutputs(Prototype9.class);
		assertNoOutputs(Prototype9.class, CDOUBLE, CSTRING);
		assertNoOutputs(Prototype9.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertNoOutputs(Prototype9.class, CFLOAT, CSTRING, CBOOLEAN);
		assertNoOutputs(Prototype9.class, CFLOAT, CSTRING);
		assertOutputs(Prototype9.class, CFLOAT);
		assertOutputs(Prototype9.class, CFLOAT, CFLOAT);
		assertOutputs(Prototype9.class, CFLOAT, CFLOAT, CFLOAT);
		assertNoOutputs(Prototype9.class, CFLOAT, CFLOAT, CINTEGER);
	}

	/**
	 */
	public void testValidate_Prototype10() {
		// {Float,String,Boolean,Integer...} fct()
		assertNoOutputs(Prototype10.class);
		assertNoOutputs(Prototype10.class, CDOUBLE, CSTRING);
		assertOutputs(Prototype10.class, CFLOAT, CSTRING, CBOOLEAN);
		assertOutputs(Prototype10.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertOutputs(Prototype10.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CINTEGER);
		assertNoOutputs(Prototype10.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CBOOLEAN);
		assertNoOutputs(Prototype10.class, CFLOAT, CSTRING);
		assertNoOutputs(Prototype10.class, CFLOAT);
	}

	/**
	 */
	public void testValidate_Prototype11() {
		// {Float,String[,Boolean],Integer...} fct()
		assertNoOutputs(Prototype11.class);
		assertNoOutputs(Prototype11.class, Double.class, CSTRING);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING, CBOOLEAN);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CINTEGER);
		assertNoOutputs(Prototype11.class, CFLOAT, CSTRING, CBOOLEAN, CINTEGER, CBOOLEAN);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING);
		assertNoOutputs(Prototype11.class, CFLOAT);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING, CINTEGER);
		assertOutputs(Prototype11.class, CFLOAT, CSTRING, CINTEGER, CINTEGER);
		assertNoOutputs(Prototype11.class, CFLOAT, CSTRING, CINTEGER, CBOOLEAN);
	}

	/**
	 * No annotation.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private interface Prototype0 {
		//
	}
	
	/**
	 * <code>void fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub
	private interface Prototype1 {
		//
	}

	/**
	 * <code>void fct(Float,String,Boolean)</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			input={Float.class,String.class,Boolean.class}
			)
	private interface Prototype2 {
		//
	}
	
	/**
	 * <code>void fct(Float,String[,Boolean])</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			input = {Float.class, String.class, Boolean.class},
			optionalInputAt = 2
			)
	private interface Prototype3 {
		//
	}

	/**
	 * <code>void fct(Float...)</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			allInput=Float.class
			)
	private interface Prototype4 {
		//
	}

	/**
	 * <code>void fct(Float,String,Boolean,Integer...)</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			input = {Float.class, String.class, Boolean.class},
			allInput = Integer.class
			)
	private interface Prototype5 {
		//
	}

	/**
	 * <code>void fct(Float,String[,Boolean],Integer...)</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			input = {Float.class, String.class, Boolean.class},
			allInput = Integer.class,
			optionalInputAt = 2
			)
	private interface Prototype6 {
		//
	}

	/**
	 * <code>{Float,String,Boolean} fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			output = {Float.class, String.class, Boolean.class}
			)
	private interface Prototype7 {
		//
	}
	
	/**
	 * <code>{Float,String[,Boolean]} fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			output = {Float.class, String.class, Boolean.class},
			optionalOutputAt = 2
			)
	private interface Prototype8 {
		//
	}

	/**
	 * <code>{Float...} fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			allOutput = Float.class
			)
	private interface Prototype9 {
		//
	}

	/**
	 * <code>{Float,String,Boolean,Integer...} fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			output = {Float.class, String.class, Boolean.class},
			allOutput = Integer.class
			)
	private interface Prototype10 {
		//
	}

	/**
	 * <code>{Float,String[,Boolean],Integer...} fct()</code>
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@PrototypeStub(
			output = {Float.class, String.class, Boolean.class},
			allOutput = Integer.class,
			optionalOutputAt = 2
			)
	private interface Prototype11 {
		//
	}

}
