package org.eclipse.lyo.validate;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.validate.impl.ValidatorImpl;
import org.eclipse.lyo.validate.shacl.ShaclShape;
import org.eclipse.lyo.validate.shacl.ShaclShapeFactory;
import org.json.JSONObject;
import org.junit.Test;

import es.weso.schema.Result;
import junit.framework.Assert;


/**
 * The Class OslcAnnotationsBasedValidationTest.
 */
public class OslcAnnotationsBasedValidationTest {

	/** The a resource. */
	AnOslcResource anOslcResource;

	/**
	 * OslcBased negative test.
	 * 
	 * This test will fail because the pattern for StringProperty does not satisfy.
	 * It should start with "B" to be valid. But Here in this example, it starts with "C".
	 */
	@Test
	public void OslcBasedNegativetest() {

		try {
			anOslcResource =  new AnOslcResource(new URI("http://www.sampledomain.org/sam#AnOslcResource"));
			anOslcResource.setAnotherIntegerProperty(new BigInteger("12"));
			anOslcResource.setAnIntegerProperty(new BigInteger("12"));
			anOslcResource.setIntegerProperty3(new BigInteger("12"));
			anOslcResource.setAStringProperty("Cat");
			anOslcResource.addASetOfDates(new Date());


			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {anOslcResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AnOslcResource.class);
			Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});
			Validator validator =  new ValidatorImpl();
			Result result = validator.validate(dataModel, shapeModel);

			JSONObject obj = new JSONObject(result.toJsonString2spaces());
			String actualError =  obj.getJSONArray("details").getJSONObject(0).getString("error").split(" ")[0];

			Assert.assertFalse(result.isValid());
			String expectedError = "sh:minCountError";
			Assert.assertEquals(expectedError, actualError );
			Assert.assertEquals(1, result.errors().size());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}


	}

	/**
	 * OslcBased positive test.
	 *
	 */
	@Test
	public void OslcBasedPositivetest() {

		try {
			anOslcResource =  new AnOslcResource(new URI("http://www.sampledomain.org/sam#anOslcResource"));
			anOslcResource.setAnotherIntegerProperty(new BigInteger("12"));
			anOslcResource.setAnIntegerProperty(new BigInteger("12"));
			anOslcResource.setIntegerProperty3(new BigInteger("12"));
			anOslcResource.setAStringProperty("Cat");
			anOslcResource.addASetOfDates(new Date());
			anOslcResource.setIntegerProperty2(new BigInteger("12"));

			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {anOslcResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AnOslcResource.class);
			Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});

			Validator validator =  new ValidatorImpl();
			Result result = validator.validate(dataModel, shapeModel);

			Assert.assertTrue(result.isValid());
			Assert.assertEquals(0, result.errors().size());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}
	}

}