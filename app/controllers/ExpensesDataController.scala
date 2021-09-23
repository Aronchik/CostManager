package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.io._
import java.text.NumberFormat
import javax.inject._
import scala.io.Source

@Singleton
class ExpensesDataController @Inject() (cc: ControllerComponents) extends AbstractController(cc){
  private val file = "Expenses.json"
  private val formatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  /**
   * Create an Action to send the client side a JSON that contains all the relevant data that is stored in the file.
   */
  def expenses: Action[AnyContent] = Action { Ok(expenseData) }

  /**
   * Create an Action to send the client side a total of the expenses.
   */
  def total: Action[AnyContent] = Action {
    Ok(formatter.format(sumAllExpenses))
  }

  /**
   * Create an Action to send the client side an average of the expenses.
   */
  def average: Action[AnyContent] = Action {
    Ok(formatter.format(averageExpenses))
  }

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  private def expenseData: JsValue = {
    val source = Source.fromFile(file)
    val data = try source.getLines.mkString finally source.close()
    Json.parse(data)
  }

  /**
   * save the current Expenses to a Json file
   * @param dataToSave - The current overall expenses we want to save
   */
  private def saveToJson(dataToSave: JsValue): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(dataToSave.toString())
    writer.close()
  }

  /**
   * Add an expense to all the current expenses
   * @param expenseToAdd - The expense we want to add
   */
  private def addExpense(expenseToAdd: JsValue): Unit = {
    val newExpenses = expenseData.as[JsArray].append(expenseToAdd)
    saveToJson(newExpenses)
  }

  /**
   * Recieve a string from a client using a POST and add it to the server stored JSON.
   * @param expenseToAdd - The expense we want to add
   */
  def addExpenseFromClient (expenseToAdd: String): Action[AnyContent] = Action {
    val replacements = Map("%22".r -> "\"", "%7B".r -> "{", "%7D".r -> "}", "%20".r -> " ")
    val replaced = replacements.foldLeft(expenseToAdd) { (s, r) => r._1.replaceAllIn(s, r._2) }
    addExpense(Json.parse(replaced))
    Ok(expenseData)
  }

  /**
   * The function calculates the sum of all the expenses
   * @return the sum of all the expenses
   */
  private def sumAllExpenses: Double = {
    val data =  (expenseData \\ "amount").map(_.as[String])
    val expenses = data.flatMap(_.toDoubleOption)
    println(expenses)
    expenses.sum
  }

  /**
   * The function calculates the average of the expenses
   * @return the average of the expenses
   */
  private def averageExpenses: Double = {
    val length = expenseData.as[List[JsObject]].length

    // Changing precision to two decimal places
    (sumAllExpenses/length * 100).round / 100.toDouble
  }
}
