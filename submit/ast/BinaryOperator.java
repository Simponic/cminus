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
public class BinaryOperator extends AbstractNode implements Expression {

  private final Expression lhs, rhs;
  private final BinaryOperatorType type;

  public BinaryOperator(Expression lhs, BinaryOperatorType type,
                        Expression rhs) {
    this.lhs = lhs;
    this.type = type;
    this.rhs = rhs;
  }

  public BinaryOperator(Expression lhs, String type, Expression rhs) {
    this.lhs = lhs;
    this.type = BinaryOperatorType.fromString(type);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    lhs.toCminus(builder, prefix);
    builder.append(" ").append(type).append(" ");
    rhs.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    MIPSResult left = lhs.toMIPS(code, data, symbolTable, regAllocator);
    String leftRegister =
        regAllocator.getRegisterOrLoadIntoRegister(left, code);

    MIPSResult right = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rightRegister =
        regAllocator.getRegisterOrLoadIntoRegister(right, code);
    String resultRegister = regAllocator.getAny();

    switch (type) {
    case PLUS:
      code.append(String.format("add %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case MINUS:
      code.append(String.format("sub %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case TIMES:
      code.append(String.format("mult %s %s\n", leftRegister, rightRegister))
          .append(String.format("mflo %s\n", resultRegister));
      break;
    case DIVIDE:
      code.append(String.format("div %s %s\n", leftRegister, rightRegister))
          .append(String.format("mflo %s\n", resultRegister));
      break;
    case MOD:
      code.append(String.format("div %s %s\n", leftRegister, rightRegister))
          .append(String.format("mfhi %s\n", resultRegister));
      break;
    case LT:
      code.append(String.format("slt %s %s %s\n", resultRegister, leftRegister,
                                rightRegister))
          .append(
              String.format("subi %s %s 1\n", resultRegister, resultRegister));
      break;
    case GT:
      code.append(String.format("slt %s %s %s\n", resultRegister, rightRegister,
                                leftRegister))
          .append(
              String.format("subi %s %s 1\n", resultRegister, resultRegister));
      break;
    case LE:
      code.append(String.format("slt %s %s %s\n", resultRegister, rightRegister,
                                leftRegister));
      break;
    case GE:
      code.append(String.format("slt %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case EQ:
      code.append(String.format("sub %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case OR:
      code.append(String.format("or %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case AND:
      code.append(String.format("abs %s %s\n", leftRegister, leftRegister))
          .append(String.format("abs %s %s\n", rightRegister, rightRegister))
          .append(String.format("add %s %s %s\n", resultRegister, leftRegister,
                                rightRegister));
      break;
    case NE:
      code.append(String.format("xor %s %s %s\n", resultRegister, leftRegister,
                                rightRegister))
          .append(
              String.format("sltiu %s %s 1\n", resultRegister, resultRegister));
    default:
      break;
    }

    regAllocator.clear(leftRegister);
    regAllocator.clear(rightRegister);

    return MIPSResult.createRegisterResult(resultRegister, VarType.INT);
  }
}
