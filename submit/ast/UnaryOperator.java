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
public class UnaryOperator extends AbstractNode implements Expression {

  private final UnaryOperatorType type;
  private final Expression expression;

  public UnaryOperator(String type, Expression expression) {
    this.type = UnaryOperatorType.fromString(type);
    this.expression = expression;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(type);
    expression.toCminus(builder, prefix);
  }
  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    MIPSResult result =
        expression.toMIPS(code, data, symbolTable, regAllocator);

    String reg = regAllocator.getRegisterOrLoadIntoRegister(result, code);

    switch (type) {
    case NEG:
      code.append(String.format("sub %s $zero %s\n", reg, reg));
      break;
    case NOT:
      code.append(String.format("sltu %s $zero %s\n", reg, reg))
          .append(String.format("xori %s %s 1\n", reg, reg));
      break;
    default:
      break;
    }

    return MIPSResult.createRegisterResult(reg, VarType.INT);
  }
}
