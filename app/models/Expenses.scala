package models

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source

class Expenses {
  private val file = "Expenses.json"

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  def expenseData: JsValue = {
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
   * The function calculates the sum of all the expenses
   * @return the sum of all the expenses
   */
  def sumAllExpenses: Double = {
    val data =  (expenseData \\ "amount").map(_.as[String])
    val expenses = data.flatMap(_.toDoubleOption)
    println(expenses)
    expenses.sum
  }

  /**
   * The function calculates the average of the expenses
   * @return the average of the expenses
   */
  def averageExpenses: Double = {
    val length = expenseData.as[List[JsObject]].length

    // Changing precision to two decimal places
    (sumAllExpenses/length * 100).round / 100.toDouble
  }

  /**
   * Recieves a String processes it and adds it to the server stored JSON.
   * @param expenseToAdd - The expense we want to add
   */
  def addExpenseFromClient (expenseToAdd: String): JsValue = {
    val replacements = Map("%22".r -> "\"", "%7B".r -> "{", "%7D".r -> "}", "%20".r -> " ")
    val replaced = replacements.foldLeft(expenseToAdd) { (s, r) => r._1.replaceAllIn(s, r._2) }
    addExpense(Json.parse(replaced))
    expenseData
  }
}
