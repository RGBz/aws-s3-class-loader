package st.engineer.tools.aws;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonS3ClassLoader extends ClassLoader {
	
	private static final String S3_URL_PREFIX = "https://s3.amazonaws.com/";
	
	private AmazonS3Client s3;
	private String bucketName;
	
	public AmazonS3ClassLoader(String bucketName, Region region) {
		this.bucketName = bucketName;
		s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
        s3.setRegion(region);
	}
	
	@Override
	public URL getResource(String name) {
		try {
			return new URL(S3_URL_PREFIX + bucketName + "/" + name);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		return s3.getObject(bucketName, name).getObjectContent();
	}

	public String getBucketName() {
		return bucketName;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] classBytes = getClassBytes(name);
		return defineClass(name, classBytes, 0, classBytes.length);
	}
	
	private byte[] getClassBytes(String key) throws ClassNotFoundException {
		try {
			int contentLength = (int) s3.getObjectMetadata(bucketName, key).getContentLength();
			byte[] bytes = new byte[contentLength];
			getResourceAsStream(key).read(bytes);
			return bytes;
		}
		catch (Throwable t) {
			throw new ClassNotFoundException("Unable to find class resource for key \"" + key + "\" in S3 Bucket \"" + bucketName + "\"", t);
		}
	}
}
