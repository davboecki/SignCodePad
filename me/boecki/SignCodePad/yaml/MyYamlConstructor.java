package me.boecki.SignCodePad.yaml;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import me.boecki.SignCodePad.Calibration;
import me.boecki.SignCodePad.CalibrationSettings;
import me.boecki.SignCodePad.SettingsSave;
import me.boecki.SignCodePad.SignLoc;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

public class MyYamlConstructor extends Constructor {
    private HashMap<String,Class<?>> classMap = new HashMap<String,Class<?>>();

       public MyYamlConstructor(Class<? extends Object> theRoot) {
           super( theRoot );
           classMap.put( SettingsSave.class.getName(), SettingsSave.class );
           classMap.put( SignLoc.class.getName(), SignLoc.class );
           classMap.put( Calibration.class.getName(), Calibration.class );
           classMap.put( CalibrationSettings.class.getName(), CalibrationSettings.class );
       }


       /*
        * This is a modified version of the Constructor. Rather than using a class loader to
        * get external classes, they are already predefined above. This approach works similar to
        * the typeTags structure in the original constructor, except that class information is
        * pre-populated during initialization rather than runtime.
        *
        * @see org.yaml.snakeyaml.constructor.Constructor#getClassForNode(org.yaml.snakeyaml.nodes.Node)
        */
        protected Class<?> getClassForNode(Node node) {
            String name = node.getTag().getClassName();
            Class<?> cl = classMap.get( name );
            if ( cl == null )
                throw new YAMLException( "Class not found: " + name );
            else
                return cl;
        }
}