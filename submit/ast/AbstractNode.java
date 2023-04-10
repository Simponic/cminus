package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

public class AbstractNode implements Node {
    public void toCminus(StringBuilder sb, String prefix) {
    }

    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable,
            RegisterAllocator regAllocator) {
        return null;
    }
}
