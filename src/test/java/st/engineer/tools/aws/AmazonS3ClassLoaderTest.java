package st.engineer.tools.aws;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonS3ClassLoaderTest extends TestCase {
	
	private static final String TEST_BUCKET_NAME = "engineer-source";
	private static final String TEST_CLASS_RESOURCES_DIR = "src/test/resources";
	private static final String[] TEST_CLASS_NAMES = new String[] {
		"rgbz.Coordinate",
		"rgbz.Rectangle"
	};
	
	private ClassLoader classLoader;
	private AmazonS3Client s3;
	
	@Before
	public void setUp() {
		Region region = Region.getRegion(Regions.US_EAST_1);
		classLoader = new AmazonS3ClassLoader(TEST_BUCKET_NAME, Region.getRegion(Regions.US_EAST_1));
		s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
        s3.setRegion(region);
        uploadTestClassFiles();
	}
	
	@After
	public void tearDown() {
		deleteTestClassFiles();
	}
	
	@Test
	public void testClassLoading() throws ClassNotFoundException {
		Class<?> clazz = classLoader.loadClass("rgbz.Coordinate");
		assertEquals(clazz.getName(), "rgbz.Coordinate");
	}
	
	@Test
	public void testClassLoadingWithDependencies() throws ClassNotFoundException {
		Class<?> clazz = classLoader.loadClass("rgbz.Rectangle");
		assertEquals(clazz.getName(), "rgbz.Rectangle");
	}
	
	@Test
	public void testResourceLoading() throws IOException {
		assertNotNull(classLoader.getResource("rgbz.Coordinate.class"));
		assertTrue(IOUtils.contentEquals(
				FileUtils.openInputStream(new File(TEST_CLASS_RESOURCES_DIR + "/rgbz.Coordinate.class")),
				classLoader.getResourceAsStream("rgbz.Coordinate.class")));
	}
	
	@Test
	public void testClassNotFound() {
		try {
			classLoader.loadClass("rgbz.Coordinate2");
			fail("Class should not exist");
		}
		catch (ClassNotFoundException e) {
			
		}
	}
	
	private void uploadTestClassFiles() {
		for (String className : TEST_CLASS_NAMES) {
			File classFile = new File(TEST_CLASS_RESOURCES_DIR + "/" + className + ".class");
			s3.putObject(TEST_BUCKET_NAME, classFile.getName(), classFile);
		}
	}
	
	private void deleteTestClassFiles() {
		for (String className : TEST_CLASS_NAMES) {
			s3.deleteObject(TEST_BUCKET_NAME, className + ".class");
		}
	}
}
