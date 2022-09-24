import java.util.HashMap;

public class MapOfIns {
    int numOfReg = 0;
    private HashMap<String, String> mapOfIns;

    public MapOfIns() {
        this.mapOfIns = new HashMap<String, String>();
    }

    public void setMapofIns(String Equation, String type) {
        mapOfIns.put(Equation, generateTinyIns(Equation, type));
    }

    public String getTinyCode(String Equation) {
        String tiny = mapOfIns.get(Equation);
        return tiny;
    }

    public String generateTinyIns(String Equation, String type) {
        String output = "";
        String[] SplitEquation = Equation.split(" ");
        int SplEqLenght = SplitEquation.length;
        if (SplEqLenght == 3) {
            String val = SplitEquation[2];
            String var = SplitEquation[0];
            String reg = generateReg();
            output = "move " + val + " " + reg + "\n" +
                    "move " + reg + " " + var;
        } else if (SplEqLenght == 5) {
            String val1 = SplitEquation[2];
            String var = SplitEquation[0];
            String val2 = SplitEquation[4];
            String reg = generateReg();
            String operation = "";
            switch (SplitEquation[3] + type) {
                case "+INT":
                    operation = "addi";
                    break;
                case "+FLOAT":
                    operation = "addr";
                    break;
                case "-INT":
                    operation = "subi";
                    break;
                case "-FLOAT":
                    operation = "subr";
                    break;
                case "*INT":
                    operation = "muli";
                    break;
                case "*FLOAT":
                    operation = "mulr";
                    break;
                case "/INT":
                    operation = "divi";
                    break;
                case "/FLOAT":
                    operation = "divr";
                    break;
            }

            output = "move " + val1 + " " + reg + "\n" +
                    operation + " " + val2 + " " + reg + "\n" +
                    "move " + reg + " " + var;
        } else {

            if (SplitEquation[0].contains("WRITE")) {
                switch (type) {
                    case "INT":
                        output = "sys writei " + SplitEquation[1];
                        break;
                    case "FLOAT":
                        output = "sys writer " + SplitEquation[1];
                        break;
                    case "STRING":
                        output = "sys writes " + SplitEquation[1];
                        break;
                }
            } else {
                switch (type) {
                    case "INT":
                        output = "sys readi " + SplitEquation[1];
                        break;
                    case "FLOAT":
                        output = "sys readr " + SplitEquation[1];
                        break;
                }
            }
        }
        return output;
    }

    private String generateReg() {
        String reg = "r" + numOfReg;
        numOfReg++;
        return reg;
    }
    /*
     * public class Equation
     * {
     * private String variable;
     * private String operand;
     * private String value;
     * private String value2;
     * private String type;
     * //private boolean pran;
     * private boolean write;
     * private boolean read;
     * 
     * public Equation(String var, String val, String type)
     * {
     * this.variable = var;
     * this.value = val;
     * this.type = type;
     * }
     * 
     * public Equation(String var, String val1, String op, String val2, String type)
     * {
     * this.variable = var;
     * this.value = val1;
     * this.operand = op;
     * this.value2 = val2;
     * this.type = type;
     * }
     * 
     * public Equation(String vars, boolean read, boolean write, String type)
     * {
     * this.variable = vars;
     * this.write = write;
     * this.read = read;
     * this.type = type;
     * }
     * 
     * public String getVariable()
     * {
     * return variable;
     * }
     * 
     * public String getValue()
     * {
     * return value;
     * }
     * 
     * public String get2Value()
     * {
     * return value2;
     * }
     * 
     * public String getOp()
     * {
     * return operand;
     * }
     * }
     */
    /*
     * public class TinyIns
     * {
     * private String variable;
     * private String value;
     * private String operation;
     * private String register;
     * 
     * public TinyIns(String var, String val, String op, String type)
     * {
     * this.variable = var;
     * this.value = val;
     * this.operation = generateOp(op, type);
     * }
     * 
     * public TinyIns()
     * {
     * this.variable = null;
     * this.value = null;
     * this.operation = null;
     * }
     * 
     * private String generateOp(String op, String type) {
     * String operation = "";
     * 
     * switch (op + type)
     * {
     * case "+INT": operation = "addi";
     * break;
     * case "+FLOAT": operation = "addr";
     * break;
     * case "-INT": operation = "subi";
     * break;
     * case "-FLOAT": operation = "subr";
     * break;
     * case "*INT": operation = "muli";
     * break;
     * case "*FLOAT": operation = "mulr";
     * break;
     * case "/INT": operation = "divi";
     * break;
     * case "/FLOAT": operation = "divr";
     * break;
     * }
     * 
     * return operation;
     * }
     * 
     * private String generateReg()
     * {
     * String reg = "r" + numOfReg;
     * numOfReg++;
     * return reg;
     * }
     * 
     * public void setVar(String var)
     * {
     * this.variable = var;
     * }
     * 
     * public void setVal(String val)
     * {
     * this.value = val;
     * }
     * 
     * public void setOp(String op, String type)
     * {
     * this.operation = generateOp(op, type);
     * }
     * 
     * public void setReg(String reg)
     * {
     * this.register = generateReg();
     * }
     * }
     */

}
