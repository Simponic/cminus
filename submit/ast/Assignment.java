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
public class Assignment extends AbstractNode implements Expression {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());
    }
  }

  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    MIPSResult mut = mutable.toMIPS(code, data, symbolTable, regAllocator);
    MIPSResult result = rhs.toMIPS(code, data, symbolTable, regAllocator);

    String registerWithAddressOfMutable = mut.getAddress();

    if (result.getRegister() != null) {
      code.append(String.format("sw %s 0(%s)\n", result.getRegister(),
                                registerWithAddressOfMutable));
      regAllocator.clear(result.getRegister());
    } else if (result.getAddress() != null) {
      regAllocator.loadIntoRegister(result, code, result.getAddress());
      regAllocator.clear(result.getAddress());
    }

    regAllocator.clear(registerWithAddressOfMutable);

    return MIPSResult.createVoidResult();
  }
}
