package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.Context
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp
import org.camunda.feel.script.CompiledFeelScript
import org.camunda.feel.script.CompiledFeelScript

/**
 * @author Philipp Ossler
 */
class FeelEngine {

  val interpreter = new FeelInterpreter

  def evalExpression(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalSimpleUnaryTests(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    eval(FeelParser.parseSimpleUnaryTests, expression, context)
  }
  
  def eval(exp: Exp, context: Map[String, Any] = Map()): EvalResult = evalParsedExpression(exp, context)
  
  private def eval(parser: String => ParseResult[Exp], expression: String, context: Map[String, Any]) = parser(expression) match {
    case Success(exp, _) => evalParsedExpression(exp, context)
    case e: NoSuccess => ParseFailure(s"failed to parse expression '$expression':\n$e")
  }
  
  private def evalParsedExpression(exp: Exp, context: Map[String, Any]): EvalResult = interpreter.eval(exp)(Context(context)) match {
    case ValError(cause) => EvalFailure(s"failed to evaluate expression '$exp':\n$cause")
    case value => EvalValue( ValueMapper.unpackVal(value) )
  }
   
}