import java.io.File;
import java.io.FileWriter; 
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Type;

public class Generator_stub {

    public static String getVarName(String typeStr){
        return typeStr.toLowerCase().substring(3) +"_var";
    }

    public static void main(String[] args){
        String classeName = args[0];
        String stubName = classeName+"_stub";
        File javaFile = new File(stubName+".java");

        try{
            Class<?> classe = Class.forName(classeName);
            FileWriter myWriter = new FileWriter(javaFile);

            myWriter.write("import java.rmi.RemoteException;\n\n");
            myWriter.write("public class "+stubName+" extends SharedObject implements "+classeName+"_itf, java.io.Serializable {\n\n");


            //Constructeur du ShareObject
            myWriter.write("\tpublic "+stubName+"(Client client, int id, Object object) throws RemoteException {\n");
            myWriter.write("\t\tsuper(client, id, object);\n");
            myWriter.write("\t}\n");

            Method[] methodes = classe.getDeclaredMethods();
            for (Method m : methodes){
                String modifiers = Modifier.toString(m.getModifiers());
                Type mType = m.getReturnType();
                String mName = m.getName();
                TypeVariable[] types = m.getTypeParameters();

                myWriter.write("\t"+modifiers+" "+mType+" "+mName+"(");

                for (int j =0; j<types.length;j++){
                    TypeVariable t = types[j];
                    myWriter.write(t.getTypeName()+" "+t.getName());
                    if(j+1 < types.length){
                        myWriter.write(", ");
                    }
                }
                myWriter.write(") {\n");
                
                myWriter.write("\t\t"+classeName+" s = ("+classeName+") obj;\n");
                myWriter.write("\t\t");
                if(!mType.equals(Void.TYPE)){
                    myWriter.write("return ");
                }
                myWriter.write("s."+m.getName()+"(");
                for (int j =0; j<types.length;j++){
                    TypeVariable t = types[j];
                    myWriter.write(t.getName());
                    if(j+1 < types.length){
                        myWriter.write(", ");
                    }
                }
                myWriter.write(");\n");
                myWriter.write("\t}\n");

            }

            myWriter.write("\n}");
            myWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}