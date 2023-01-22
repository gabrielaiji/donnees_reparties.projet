import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.Parameter;

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

            //Méthodeds du ShareObject
            Method[] methodes = classe.getDeclaredMethods();
            for (Method m : methodes){
                String modifiers = Modifier.toString(m.getModifiers());
                Type mType = m.getReturnType();
                String mName = m.getName();
                Parameter[] parameters = m.getParameters();

                myWriter.write("\t"+modifiers+" "+mType.getTypeName()+" "+mName+"(");

                for (int j = 0 ; j < parameters.length ; j++) {
                    Parameter p = parameters[j];
                    myWriter.write(p.getType().getTypeName()+" "+p.getName());

                    if (j+1 < parameters.length){
                        myWriter.write(", ");
                    }
                }

                myWriter.write(") {\n");
                
                myWriter.write("\t\t"+classeName+" s = ("+classeName+")obj;\n");
                myWriter.write("\t\t");
                if(!mType.equals(Void.TYPE)){
                    myWriter.write("return ");
                }
                myWriter.write("s."+m.getName()+"(");
                for (int j = 0 ; j < parameters.length ; j++) {
                    Parameter p = parameters[j];
                    myWriter.write(p.getName());

                    if (j+1 < parameters.length){
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