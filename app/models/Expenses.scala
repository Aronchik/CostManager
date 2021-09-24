package models

import play.api.libs.json.{JsArray, JsValue, Json}

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source


class Expenses {
  private val file = "Expenses.json"

  /**
   * This function opens the stored JSON file on the server that contains the expenses Information
   * @return A JsValue that can be processed by the client side.
   */
  def expenseData: JsValue = {
    val source = Source.fromFile(file)
    val data = try source.getLines.mkString finally source.close()
    Json.parse(data)
  }

  /**
   * The function extracts the expense amounts from the JSON data file
   * @return A collection of all the amounts, each amount is a double
   */
  private def extractAmounts: collection.Seq[Double] = {
    (expenseData \\ "amount").map(_.as[String]).flatMap(_.toDoubleOption)
  }

  /**
   * The function extracts the currencies from the JSON data file
   * @return A collection of all the currencies, each currency is a string
   */
  private def extractCurrencies: collection.Seq[String] = {
    (expenseData \\ "currency").map(_.as[String])
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
   * @return A map that represents the sum of all the expenses for each currency.
   */
  private def sumAllExpenses: Map[String, Double] = {
    val expenseAmounts = extractAmounts
    val currencies = extractCurrencies

    // Merging the two collections into one so we can sum the values according to currencies
    val merged = expenseAmounts.zip(currencies).map { case (a, b) => (a,b) }

    // Grouping by the currency and summing the values, returning a map of currency -> total
    merged.groupBy(_._2).map { case (k,v) => k -> (v map (_._1)).sum }
  }

  /**
   * The function calculates the average of the expenses
   * @return A map that represents the average of all the expenses for each currency.
   */
  private def averageAllExpenses: Map[String, Double] = {
    val expenseAmounts = sumAllExpenses
    val currencies = extractCurrencies

    // Getting a count for how many times each currency is used.
    val currenciesCount = currencies.groupBy(identity).map { case (v, l) => (v, l.size) }

    // Dividing the total amount for each currency by the times the currency is used. Precision is to 2 decimal places
    currenciesCount.map { case (k, v) => (k, ((expenseAmounts.getOrElse(k, 1.0) / v) * 100).round / 100.toDouble)}
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

  /**
   * The function formats the total expenses calculation and returns it as a string.
   * @return A string representing all currencies and their total expense.
   */
  def sumOfExpenses: String = {
    sumAllExpenses.map(_.productIterator.mkString(": "))
      .mkString(" | ")
  }

  /**
   * The function formats the calculation of currency averages and returns it as a string.
   * @return A string representing all currencies and their average expense.
   */
  def averageOfExpenses: String = {
    averageAllExpenses.map(_.productIterator.mkString(": "))
      .mkString(" | ")
  }
}
