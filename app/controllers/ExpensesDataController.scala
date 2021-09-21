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
  addExpense(Json.parse("""{ "id": 60, "expenseDate": "22.22.2222", "expense": "Trip to Space" , "amount": "80000", "paymentType": "Cash", "category": "Travel" , "currency": "UEC" }"""))

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  def expenseData: JsValue = {
    val data = Source.fromFile(file).getLines.mkString
    Json.parse(data)
    /**Json.parse("""
    [
        { "id": 10, "expenseDate": "12.02.2010", "expense": "Coffee" , "amount": "4.22", "paymentType": "Cash", "category": "Food", "currency": "NIS" },
        { "id": 20, "expenseDate": "12.02.2015", "expense": "Bagel" , "amount": "7.99", "paymentType": "Cash", "category": "Food" , "currency": "NIS" },
        { "id": 30, "expenseDate": "12.02.2011", "expense": "New Car" , "amount": "40800", "paymentType": "Bank Transfer", "category": "Transport" , "currency": "NIS" },
        { "id": 40, "expenseDate": "12.03.2011", "expense": "New Tires" , "amount": "2500", "paymentType": "Credit Card", "category": "Transport" , "currency": "NIS" },
        { "id": 50, "expenseDate": "12.02.2012", "expense": "Overseas Trip" , "amount": "8000", "paymentType": "Credit Card", "category": "Travel" , "currency": "NIS" }
    ]
  """)**/
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

}
