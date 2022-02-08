package de.davboecki.signcodepad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MinecraftBridge {
	protected String preCbVersion = null;
	protected String cbVersion = null;
	protected String mcVersion = null;
	protected String mcFolderVersion = null;

	protected static Map<String, String> cbMapping = new HashMap<String, String>();

	public MinecraftBridge() throws PluginOutOfDateException {
		InputStream craftbukkitPom = null;
		try {
			final ZipInputStream craftbukkitJar = new ZipInputStream(
					new FileInputStream(Bukkit.class.getProtectionDomain().getCodeSource().getLocation()
							.getPath()));
			ZipEntry entry;
			while ((entry = craftbukkitJar.getNextEntry()) != null) {
				if (entry.getName().equals("META-INF/maven/org.bukkit/craftbukkit/pom.xml")) { // CraftBukkit pom. equals string
																																												// might be outdated!
					craftbukkitPom = craftbukkitJar;
					preCbVersion = "CraftBukkit";
					break;
				} else if (entry.getName().equals("META-INF/maven/org.spigotmc/spigot-api/pom.xml")) { // Supporting Spigot
					craftbukkitPom = craftbukkitJar;
					preCbVersion = "Spigot";
					break;
				}
			}

			if (craftbukkitPom != null) {
				Document xmlDoc;
				try {
					xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(craftbukkitPom);
				} catch (final Exception e) {
					e.printStackTrace();
					return;
				}

				final Element projectNodes = xmlDoc.getDocumentElement();
				final NodeList versionNodes = projectNodes.getElementsByTagName("version");
				for (int i = 0; i < versionNodes.getLength(); i++) {
					if (versionNodes.item(i).getParentNode().equals(projectNodes)) {
						cbVersion = versionNodes.item(i).getTextContent();
						break;
					}
				}

				/*
				 * IMPORTANT:
				 * mc.version and minecraft_version was null. Using version as Both mcVersion
				 * and mcFolderVersion!
				 */

				// final Element propertiesNodes = (Element)
				// projectNodes.getElementsByTagName("properties").item(0);
				mcVersion = projectNodes.getElementsByTagName("version").item(0).getTextContent(); // minecraft.version was null... used to be: propertiesNodes.getElementsByTagName("minecraft_version").item(0).getTextContent();
				// propertiesNodes.getElementsByTagName("minecraft.version").item(0).getTextContent();
				mcFolderVersion = "v" + projectNodes.getElementsByTagName("version").item(0).getTextContent();
				craftbukkitJar.close();
			} else {
				craftbukkitJar.close();
				throw new PluginOutOfDateException(new FileNotFoundException("META-INF/maven/org.bukkit/craftbukkit/pom.xml"));
			}
		} catch (final FileNotFoundException e1) {
			throw new PluginOutOfDateException("Could not find CraftBukkit jar", e1);
		} catch (final IOException e1) {
			e1.printStackTrace();
			return;
		}
	}

	protected void add(String code, String real) {
		cbMapping.put(code, real);
	}

	/**
	 * @return the running CraftBukkit version number
	 */
	public String getPreCraftbukkitVersion() {
		return preCbVersion;
	}

	/**
	 * @return the running CraftBukkit version number
	 */
	public String getCraftbukkitVersion() {
		return cbVersion;
	}

	/**
	 * @return the running Minecraft version number
	 */
	public String getMinecraftVersion() {
		return mcVersion;
	}

	/**
	 * @return the running Minecraft version string
	 */
	public String getMinecraftFolderVersion() {
		return mcFolderVersion;
	}

	public static Class<?> loadClass(String aClass) throws PluginOutOfDateException {
		try {
			return Bukkit.class.getClassLoader().loadClass(aClass);
		} catch (final ClassNotFoundException e) {
			throw new PluginOutOfDateException("Could not find class", e);
		}
	}

	public static Object invokeMethod(String aClass, String method, Object instance, Class<?>[] parameterTypes,
			Object... parameterArgs)
			throws PluginOutOfDateException {
		if (aClass != null && method != null) {
			try {
				final Class<?> bClass = loadClass(aClass);
				if (parameterTypes == null) {
					return bClass.getDeclaredMethod(method).invoke(instance);
				} else {
					return bClass.getDeclaredMethod(method, parameterTypes).invoke(instance, parameterArgs);
				}
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				throw new PluginOutOfDateException("Could not run method", e);
			} catch (final NoSuchMethodException e) {
				throw new PluginOutOfDateException("Could not find method", e);
			} catch (final SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Object getField(String aClass, String field, Object instance) throws PluginOutOfDateException {
		if (aClass != null && field != null) {
			try {
				final Class<?> bClass = loadClass(aClass);
				return bClass.getDeclaredField(field).get(instance);
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final NoSuchFieldException e) {
				throw new PluginOutOfDateException("Could not get field", e);
			} catch (final SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void setField(String aClass, String field, Object instance, Object value)
			throws PluginOutOfDateException {
		if (aClass != null && field != null) {
			boolean accessible = true;
			boolean finalField = false;
			try {
				final Class<?> bClass = loadClass(aClass);
				final Field declaredField = bClass.getDeclaredField(field);
				final Field modifiersField = Field.class.getDeclaredField("modifiers");
				final int modifiers = declaredField.getModifiers();
				if (!declaredField.isAccessible()) {
					declaredField.setAccessible(true);
					accessible = false;
				}
				if ((modifiers & Modifier.FINAL) != 0) {
					modifiersField.setAccessible(true);
					modifiersField.setInt(declaredField, modifiers & ~Modifier.FINAL);
					finalField = true;
				}

				// Setting the actual field
				declaredField.set(instance, value);

				if (finalField) {
					modifiersField.setInt(declaredField, modifiers);
				}
				if (!accessible) {
					declaredField.setAccessible(false);
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final NoSuchFieldException e) {
				throw new PluginOutOfDateException("Could not get field", e);
			} catch (final SecurityException e) {
				System.out.println("Failed to access" + (!accessible ? " private" : "") + (finalField ? " final" : "")
						+ " field \"" + field + "\" in "
						+ aClass);
				e.printStackTrace();
			}
		}
	}

	public static Object callConstructor(String aClass, Class<?>[] parameterTypes, Object... parameterArgs)
			throws PluginOutOfDateException {
		if (aClass != null) {
			try {
				final Class<?> bClass = loadClass(aClass);
				if (parameterTypes == null) {
					return bClass.getConstructor().newInstance();
				} else {
					return bClass.getConstructor(parameterTypes).newInstance(parameterArgs);
				}
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			} catch (final SecurityException e) {
				e.printStackTrace();
			} catch (final InstantiationException e) {
				throw new PluginOutOfDateException("Cannot instantiate", e);
			} catch (final InvocationTargetException e) {
				throw new PluginOutOfDateException("Could not run constructor", e);
			} catch (final NoSuchMethodException e) {
				throw new PluginOutOfDateException("Could not find constructor", e);
			}
		}
		return null;
	}
}