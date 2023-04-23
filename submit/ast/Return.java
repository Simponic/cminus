/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Return extends AbstractNode implements Statement {
  private final Expression expr;

  public Return(Expression expr) { this.expr = expr; }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix);
    if (expr == null) {
      builder.append("return;\n");
    } else {
      builder.append("return ");
      expr.toCminus(builder, prefix);
      builder.append(";\n");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    if (expr != null) {
      String reg = regAllocator.getRegisterOrLoadIntoRegister(
          expr.toMIPS(code, data, symbolTable, regAllocator), code);
      code.append(
          String.format("# symbol table size - %d\n", symbolTable.getOffset()));
      code.append(
          String.format("sw %s %d($sp)\n", reg, symbolTable.getOffset()));
      code.append("jr $ra\n");
    }
    return MIPSResult.createVoidResult();
  }
}
