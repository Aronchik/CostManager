package controllers

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.text.NumberFormat
import javax.inject._
import models.Expenses

@Singleton
class ExpensesDataController @Inject() (cc: ControllerComponents,
                                        expenses: Expenses) extends AbstractController(cc){
  private val formatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  /**
   * Create an Action to send the client side a JSON that contains all the relevant data that is stored in the file.
   */
  def getExpenses: Action[AnyContent] = Action { Ok(expenses.expenseData) }

  /**
   * Create an Action to send the client side a total of the expenses.
   */
  def total: Action[AnyContent] = Action {
    Ok(formatter.format(expenses.sumAllExpenses))
  }

  /**
   * Create an Action to send the client side an average of the expenses.
   */
  def average: Action[AnyContent] = Action {
    Ok(formatter.format(expenses.averageExpenses))
  }

  /**
   * Recieve a string from a client using a POST and add it to the server stored JSON.
   * @param expenseToAdd - The expense we want to add
   */
  def addExpense (expenseToAdd: String): Action[AnyContent] = Action {
    Ok(expenses.addExpenseFromClient(expenseToAdd))
  }
}
