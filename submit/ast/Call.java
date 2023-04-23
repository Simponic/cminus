/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Call implements Expression {

  private final String id;
  private final List<Expression> args;

  public Call(String id, List<Expression> args) {
    this.id = id;
    this.args = new ArrayList<>(args);
  }

  private void println(List<MIPSResult> mipsResults, StringBuilder code,
                       StringBuilder data, SymbolTable symbolTable,
                       RegisterAllocator regAllocator) {
    code.append("# println\n");

    for (MIPSResult result : mipsResults) {
      if (result.getRegister() == null && result.getAddress() != null) {
        if (result.getAddress().startsWith("$")) {
          // Hack - might be a register
          regAllocator.loadIntoRegister(result, code, "$a0");
        } else {
          code.append(String.format("la $a0 %s\n", result.getAddress()));
        }
      } else if (result.getRegister() != null) {
        code.append(String.format("move $a0 %s\n", result.getRegister()));
      }

      if (result.getType() == VarType.INT) {
        code.append("li $v0 1\n");
      } else if (result.getType() == VarType.CHAR) {
        code.append("li $v0 11\n");
      } else {
        code.append("li $v0 4\n");
      }
      code.append("syscall\n");
    }

    // End line with newline
    code.append(String.format("la $a0 %s\n", "newline"));
    code.append("li $v0 4\n");
    code.append("syscall\n");
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id).append("(");
    for (Expression arg : args) {
      arg.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!args.isEmpty()) {
      builder.setLength(builder.length() - 2);
    }
    builder.append(")");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    List<MIPSResult> argMips;
    String resultRegister = null;

    if (this.id.equals("println")) {
      argMips =
          args.stream()
              .map(e -> e.toMIPS(code, data, symbolTable, regAllocator))
              .collect(Collectors.toList());
      this.println(argMips, code, data, symbolTable, regAllocator);
    } else {
      argMips = new ArrayList<MIPSResult>();
      String returnAddr = regAllocator.getAny();
      code.append(String.format("move %s $ra\n", returnAddr));
      int savedRegistersOffset = regAllocator.saveRestore(
          code, -symbolTable.getOffset(), false, true);

      int argsOffset = symbolTable.getOffset() - savedRegistersOffset;
      int i = argsOffset;
      for (Expression expression : args) {
        MIPSResult mipsResult =
            expression.toMIPS(code, data, symbolTable, regAllocator);
        argMips.add(mipsResult);
        i -= 4;

        String reg =
            regAllocator.getRegisterOrLoadIntoRegister(mipsResult, code);
        code.append(String.format("sw %s %d($sp)\n", reg, i));

        regAllocator.clear(reg);
      }

      code.append(String.format("add $sp $sp %d\n", argsOffset));

      code.append(String.format("jal %s\n", id));

      code.append(String.format("add $sp $sp %d\n", -argsOffset));

      regAllocator.saveRestore(code, -symbolTable.getOffset(), false,
                                    false);
      code.append(String.format("# symbol table size - %d\n", i));

      resultRegister = regAllocator.getAny();
      regAllocator.loadIntoRegisterWithOffset(
          MIPSResult.createAddressResult("$sp", VarType.INT), code,
          resultRegister, i - 4);

      code.append(String.format("move $ra %s\n", returnAddr));
      regAllocator.clear(returnAddr);
    }

    for (MIPSResult arg : argMips)
      if (arg.getRegister() != null)
        regAllocator.clear(arg.getRegister());

    if (resultRegister != null) {
      VarType returnType = symbolTable.find(id).getType();
      return MIPSResult.createRegisterResult(resultRegister, returnType);
    }

    return MIPSResult.createVoidResult();
  }
}
