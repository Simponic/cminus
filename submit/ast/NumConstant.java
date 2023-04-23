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
public class NumConstant extends AbstractNode implements Expression {

  private final int value;

  public NumConstant(int value) { this.value = value; }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append(Integer.toString(value));
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    String register = regAllocator.getAny();
    code.append(String.format("li %s %d\n", register, value));

    return MIPSResult.createRegisterResult(register, VarType.INT);
  }
}
