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
 * The Class ShaclPatternValidationTest.
 */
public class ShaclPatternValidationTest {

	/** The a resource. */
	AResource aResource;

	/**
	 * Shacl pattern negative test.
	 * 
	 * This test will fail because the pattern for StringProperty does not satisfy.
	 * It should start with "B" to be valid. But Here in this example, it starts with "C".
	 */
	@Test
	public void ShaclPatternNegativetest() {

		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			//Invalid Value. Should Start with 'B' to be valid.
			aResource.setAStringProperty("Cat");
			aResource.addASetOfDates(new Date());


			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
			Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});

			Validator validator =  new ValidatorImpl();
			Result result = validator.validate(dataModel, shapeModel);

			JSONObject obj = new JSONObject(result.toJsonString2spaces());
			String actualError =  obj.getJSONArray("details").getJSONObject(0).getString("error").split(" ")[0];

			Assert.assertFalse(result.isValid());
			String expectedError = "sh:patternError";
			Assert.assertEquals(expectedError, actualError );
			Assert.assertEquals(1, result.errors().size());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}


	}

	/**
	 * Shacl pattern positive test.
	 *
	 */
	@Test
	public void ShaclPatternPositivetest() {

		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			aResource.setAStringProperty("Between");
			aResource.addASetOfDates(new Date());

			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
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
