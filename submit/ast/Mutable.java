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
public class Mutable implements Expression {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }

  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    String stackReg = regAllocator.getAny();

    code.append(String.format("li %s %d\n", stackReg, symbolTable.offsetOf(id)))
        .append(String.format("add %s %s $sp\n", stackReg, stackReg));

    return MIPSResult.createAddressResult(stackReg, VarType.INT);
  }
}
