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
public class While extends AbstractNode implements Statement {

  private final Expression expression;
  private final Statement statement;

  public While(Expression expression, Statement statement) {
    this.expression = expression;
    this.statement = statement;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("while (");
    expression.toCminus(builder, prefix);
    builder.append(")\n");
    if (statement instanceof CompoundStatement) {
      statement.toCminus(builder, prefix);
    } else {
      statement.toCminus(builder, prefix + " ");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    String loopLabel = "while_truthy_" + SymbolTable.nextId();
    String finishedLabel = "finished_while_" + SymbolTable.nextId();

    code.append(String.format("%s:\n", loopLabel));

    MIPSResult expressionTruthiness =
        expression.toMIPS(code, data, symbolTable, regAllocator);

    code.append(String.format("bne %s $zero %s\n",
                              expressionTruthiness.getRegister(),
                              finishedLabel));

    MIPSResult inside = statement.toMIPS(code, data, symbolTable, regAllocator);
    regAllocator.clear(inside.getRegister());
    code.append(String.format("j %s\n", loopLabel))
        .append(String.format("%s:\n", finishedLabel));

    return MIPSResult.createVoidResult();
  }
}
