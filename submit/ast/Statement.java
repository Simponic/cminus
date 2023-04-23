/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.ArrayList;

/**
 *
 * @author edwajohn
 */
public interface Statement extends Node {
  public static CompoundStatement empty() {
    submit.SymbolTable newTable = new submit.SymbolTable();
    return new CompoundStatement(new ArrayList<>(), newTable);
  }
}
