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
public class If extends AbstractNode implements Statement {

  private final Expression expression;
  private final Statement trueStatement;
  private final Statement falseStatement;

  public If(Expression expression, Statement trueStatement,
            Statement falseStatement) {
    this.expression = expression;
    this.trueStatement = trueStatement;
    this.falseStatement = falseStatement;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("if (");
    expression.toCminus(builder, prefix);
    builder.append(")\n");
    if (trueStatement instanceof CompoundStatement) {
      trueStatement.toCminus(builder, prefix);
    } else {
      trueStatement.toCminus(builder, prefix + " ");
    }
    if (falseStatement != null) {
      builder.append(prefix).append("else\n");
      // falseStatement.toCminus(builder, prefix);
      if (falseStatement instanceof CompoundStatement) {
        falseStatement.toCminus(builder, prefix);
      } else {
        falseStatement.toCminus(builder, prefix + " ");
      }
    }
    // builder.append(prefix).append("}");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    String continueLabel = "if_" + SymbolTable.nextId();
    String elseLabel = "else_" + SymbolTable.nextId();

    MIPSResult expressionTruthiness =
        expression.toMIPS(code, data, symbolTable, regAllocator);

    code.append(String.format("bne %s $zero %s\n",
                              expressionTruthiness.getRegister(), elseLabel));
    regAllocator.clear(expressionTruthiness.getRegister());

    MIPSResult trueRes =
        trueStatement.toMIPS(code, data, symbolTable, regAllocator);
    regAllocator.clear(trueRes.getRegister());

    code.append(String.format("j %s\n", continueLabel))
        .append(String.format("%s:\n", elseLabel));

    regAllocator.clear(expressionTruthiness.getRegister());

    if (falseStatement != null) {
      MIPSResult falseRes =
          falseStatement.toMIPS(code, data, symbolTable, regAllocator);
      regAllocator.clear(falseRes.getRegister());
    }
    code.append(String.format("%s:\n", continueLabel));

    return MIPSResult.createVoidResult();
  }
}
