/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.List;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class CompoundStatement extends AbstractNode implements Statement {

  private final List<Statement> statements;
  private SymbolTable symbolTable;

  public CompoundStatement(List<Statement> statements,
                           SymbolTable symbolTable) {
    this.statements = statements;
    this.symbolTable = symbolTable;
  }

  public SymbolTable getSymbolTable() { return symbolTable; }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("{\n");
    for (Statement s : statements) {
      s.toCminus(builder, prefix + "  ");
    }
    builder.append(prefix).append("}\n");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    for (String name : this.symbolTable.symbolNames())
      code.append(
          String.format("#  %s : %d\n", name, this.symbolTable.offsetOf(name)));

    code.append("# Update the stack pointer.\n");
    code.append(String.format("addi $sp $sp %d\n", symbolTable.getOffset()));

    for (Statement statement : statements)
      statement.toMIPS(code, data, this.symbolTable, regAllocator);

    code.append("# Exit scope.\n");
    code.append(String.format("addi $sp $sp %s\n", -symbolTable.getOffset()));

    return MIPSResult.createVoidResult();
  }
}
