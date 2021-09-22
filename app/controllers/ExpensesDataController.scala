package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import java.io._
import javax.inject._
import scala.io.Source

@Singleton
class ExpensesDataController @Inject() (cc: ControllerComponents) extends AbstractController(cc){
  val file = "Expenses.json"

  /**
   * Create an Action to send the client side a JSON that contains all the relevant data that is stored in the file.
   */
  def expenses = Action { Ok(expenseData) }

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  def expenseData: JsValue = {
    val data = Source.fromFile(file).getLines.mkString
    Json.parse(data)
  }

  /**
   * save the current Expenses to a Json file
   * @param dataToSave - The current overall expenses we want to save
   */
  def saveToJson(dataToSave: JsValue) = {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(dataToSave.toString())
    writer.close()
  }

  /**
   * Add an expense to all the current expenses
   * @param expenseToAdd - The expense we want to add
   */
  def addExpense(expenseToAdd: JsValue)  = {
    val newExpenses = expenseData.as[JsArray].append(expenseToAdd)
    saveToJson(newExpenses)
  }

  /**
   * Recieve a string from a client using a POST and add it to the server stored JSON.
   * @param expenseToAdd - The expense we want to add
   */
  def addExpenseFromClient (expenseToAdd: String) = Action {
    val replacements = Map("%22".r -> "\"", "%7B".r -> "{", "%7D".r -> "}", "%20".r -> " ")
    val replaced = replacements.foldLeft(expenseToAdd) { (s, r) => r._1.replaceAllIn(s, r._2) }
    addExpense(Json.parse(replaced))
    Ok(expenseData)
  }
}
