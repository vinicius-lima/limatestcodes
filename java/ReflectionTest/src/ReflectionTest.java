import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionTest {

	public static void main(String[] args) {
		AnyClass any = new AnyClass(10, 'a', "test");
		//Class<AnyClass> ref = (Class<AnyClass>) any.getClass();
		Class<AnyClass> ref = AnyClass.class;
		//Class<AnyInterface> ref = AnyInterface.class;
		
		System.out.println("Available constructors:");
		for(Constructor<?> cons : ref.getConstructors()) {
			System.out.println("Name: " + cons.getName());
			System.out.print("Parameters: ");
			for(Class<?> pmt : cons.getParameterTypes()) {
				System.out.print(pmt.getName() + " ");
			}
			System.out.println("");
		}
		
		System.out.println("\nAvailable fields (public only):");
		for(Field field : ref.getFields()) {
			System.out.print("Name/Type/Value:" + field.getName() + "/" + field.getType().getName() + "/");
			try {
				System.out.println(field.get(any));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("\nAvailable fields (all of them):");
		for(Field field : ref.getDeclaredFields()) {
			System.out.print("Name/Type/Value:" + field.getName() + "/" + field.getType().getName() + "/");
			try {
				System.out.println(field.get(any));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out.println("Nonpublic field! Cannot access value!");
			}
		}
		
		System.out.println("\nAvailable methods (public only):");
		for(Method met : ref.getMethods()) {
			System.out.println("Name: " + met.getName());
			System.out.print("Parameters: ");
			for(Class<?> pmt : met.getParameterTypes()) {
				System.out.print(pmt.getName() + " ");
			}
			System.out.println("");
		}
		
		System.out.println("\nImplemented interfaces:");
		for(Class<?> met : ref.getInterfaces()) {
			System.out.print(met.getName() + " ");
		}
		System.out.println("");
		
		System.out.println("\nInvoke:");
		try {
			Method met = ref.getMethod("getField2", (Class<?>[])null);
			char c = (char)met.invoke(any, (Object[])null);
			System.out.println(c);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
