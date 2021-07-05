import Lexer.Lexer
import Parser.*
import Parser.ParserToken.*
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest
{
    fun CallMain(statementList: List<Statement>): Declaration.FunctionDeclare
    {
        return CallMain(null, null, statementList);
    }

    fun CallMain(localVariable: List<Declaration.VariableDeclaration>?, statementList: List<Statement>): Declaration.FunctionDeclare
    {
        return CallMain(localVariable, null, statementList);
    }

    fun CallMain(localVariables: List<Declaration.VariableDeclaration>?, parameters: List<Parameter>?, statementList: List<Statement>): Declaration.FunctionDeclare
    {
        return Declaration.FunctionDeclare(
            Type.Integer,
            "main",
            Body(statementList, localVariables),
            parameters
        )
    }

    fun TestIfTreeIsAsExpected(code : String, declaration: Declaration.FunctionDeclare)
    {
        //println(declaration)

        val lexer = Lexer(code)
        val parser = Parser(lexer)
        val parserTokenTree = parser.DoStuff()

        assertEquals(declaration, parserTokenTree)
    }


    @Test
    fun ReturnDirectTest()
    {
        val code = """
            int main()
            {
                return 5;
            }
        """.trimIndent()

        val statementList = listOf<Statement>(
            Statement.AssignValue(Type.Return, Expression.ConstValue(ConstantValue.ConstInteger(5)))
        )

        val tree = CallMain(statementList)

        TestIfTreeIsAsExpected(code, tree);
    }

    @Test
    fun ReturnWithAdditionTest()
    {
        val code = """
            int main()
            {
                return 5 + 5;
            }
        """.trimIndent()

        val statementList = listOf<Statement>(
            Statement.AssignValue(
                Type.Return,
                Expression.Calculation(
                    Operator.Plus,
                    Expression.ConstValue(ConstantValue.ConstInteger(5)),
                    Expression.ConstValue(ConstantValue.ConstInteger(5))
                ))
        )

        val tree = CallMain(statementList)

        TestIfTreeIsAsExpected(code, tree);
    }

    @Test
    fun ReturnWithDeclarationTest()
    {
        val code = """
            int main()
            {
                int §a = 0;
                
                return §a;
            }
        """.trimIndent()

        val localVariables = listOf<Declaration.VariableDeclaration>(
            Declaration.VariableDeclaration(Type.Integer, "a", Expression.ConstValue(ConstantValue.ConstInteger(0)))
        )

        val statementList = listOf<Statement>(
            Statement.AssignValue(
                Type.Return,
                Expression.UseVariable("a")
            )
        )

        val tree = CallMain(localVariables, statementList)

        TestIfTreeIsAsExpected(code, tree)
    }

    @Test
    fun ReturnWithLoopTest()
    {
        val code = """
            int main()
            {
                int §a = 1;
                
                while(§a == 5)
                {
                    §a = §a + 5;
                }
                
                return §a;
            }
        """.trimIndent()

        val localVariables = listOf<Declaration.VariableDeclaration>(
            Declaration.VariableDeclaration(Type.Integer, "a", Expression.ConstValue(ConstantValue.ConstInteger(1)))
        )

        val statementList = listOf<Statement>(
            Statement.While(
                Expression.Calculation(
                    Operator.DoubleEquals,
                    Expression.UseVariable("a"),
                    Expression.ConstValue(ConstantValue.ConstInteger(5))
                ),
                Body(
                    listOf<Statement>(
                    Statement.AssignValue(
                        Type.Variable("a"),
                        Expression.Calculation(
                            Operator.Plus,
                            Expression.UseVariable("a"),
                            Expression.ConstValue(ConstantValue.ConstInteger(5))
                        )
                    )
                 )
                )
            ),
            Statement.AssignValue(
                Type.Return,
                Expression.UseVariable("a")
            )
        )

        val tree = CallMain(localVariables, statementList)

        TestIfTreeIsAsExpected(code, tree)
    }

    @Test
    fun ReturnWithParametersTest()
    {
        val code = """
            int main(int §a, int §b)
            {
                return §a * §b;
            }
        """.trimIndent()

        val parameters = listOf<Parameter>(
            Parameter("a", Type.Integer),
            Parameter("b", Type.Integer)
        )

        val statementList = listOf<Statement>(
            Statement.AssignValue(
                Type.Return,
                Expression.Calculation(
                    Operator.Multiply,
                    Expression.UseVariable("a"),
                    Expression.UseVariable("b")
                )
            )
        )

        val tree = CallMain(null, parameters, statementList)

        TestIfTreeIsAsExpected(code, tree)
    }

    @Test
    fun ReturnWithFuncitonCallTest()
    {
        val code = """
            int main()
            {
                return 2 + §A(3,5);
            }
        """.trimIndent()

        val statementList = listOf<Statement>(
            Statement.AssignValue(
                Type.Return,
                Expression.Calculation(
                    Operator.Plus,
                    Expression.ConstValue(ConstantValue.ConstInteger(2)),
                    Expression.FunctionCall(
                        "a",
                        listOf<Expression>(
                            Expression.ConstValue(ConstantValue.ConstInteger(3)),
                            Expression.ConstValue(ConstantValue.ConstInteger(5))
                        )
                    )
                )
            )
        )

        val tree = CallMain(null, null, statementList)

        TestIfTreeIsAsExpected(code, tree)
    }

    @Test
    fun ReturnWithIFTest()
    {
        val code = """
            int main()
            {
                int §w = 3;
                bool §f = §w <= 3;
                
                if(f)
                {
                    return 1;
                }
            
                return 0;
            }
        """.trimIndent()

        val localVariables = listOf<Declaration.VariableDeclaration>(
            Declaration.VariableDeclaration(Type.Integer, "w", Expression.ConstValue(ConstantValue.ConstInteger(3))),
            Declaration.VariableDeclaration(Type.Boolean, "f", Expression.Calculation(Operator.LessEqual, Expression.UseVariable("w"), Expression.ConstValue(ConstantValue.ConstInteger(3))))
        )

        val statementList = listOf<Statement>(
            Statement.If(
                Expression.UseVariable("f"),
                Body(listOf<Statement>(
                    Statement.AssignValue(
                        Type.Return,
                        Expression.ConstValue(ConstantValue.ConstInteger(1))
                    )
                )),
                null
            ),
            Statement.AssignValue(
                Type.Return,
                Expression.ConstValue(ConstantValue.ConstInteger(0))
            )

        )

        val tree = CallMain(localVariables, statementList)

        TestIfTreeIsAsExpected(code, tree)
    }
}