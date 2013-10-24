package org.janusproject.demos.meetingscheduler.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.arakhne.afc.text.Base64Coder;

/**
 * 
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class SerializationUtil {
	public static String encode(Object o) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(Base64Coder.encode(baos.toByteArray()));
	}

	public static Object decode(String from) {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				Base64Coder.decode(from.toCharArray()));
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
