package me.limeglass.skungee.spigot.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.Expression;

public class ExpressionData {

	private Expression<?>[] expressions;
	private Map<String, Integer> syntax = new HashMap<String, Integer>();
	public boolean nullable = false;
	
	/**
	 * Register an Expression for the abstract calling class
	 * Useless on its own
	*/
	public ExpressionData(Expression<?>[] expressions, String pattern) {
		Matcher matcher = Pattern.compile("\\%([^\\%]+)\\%").matcher(pattern);
		int i = 0;
		while (matcher.find()) {
			String expression = matcher.group(1);
			while ((expression.startsWith("-")) || (expression.startsWith("~")) || (expression.startsWith("*"))) {
				if (expression.startsWith("-")) nullable = true;
				expression = expression.substring(1, expression.length());
			}
			if (expression.endsWith("s")) expression = expression.substring(0, expression.length() - 1);
			if (expression.contains("/")) expression = "object";
			expression = expression + "0";
			if (syntax.containsKey(expression)) {
				while (syntax.containsKey(expression)) {
					char lastChar = expression.charAt(expression.length() - 1);
					if (lastChar >= '0' && lastChar <= '9') {
						expression = expression.replace(lastChar, '\0');
						expression = expression + ((int)lastChar + 1);
					}
				}
			}
			syntax.put(expression, i);
			i++;
		}
		this.expressions = expressions;
	}
	
	public ExpressionData(Expression<?>[] expressions) {
		this.expressions = expressions;
	}
	
	/**
	 * Check if any of the expressions can be null
	 * 
	 * @return boolean
	*/
	public boolean isNullable() {
		return nullable;
	}
	
	/**
	 * Get the count of syntax registered to ExpressionData.
	 * 
	 * @return int
	*/
	@Nullable
	public int size() {
		return syntax.size();
	}
	
	/**
	 * Grabs the expression associated with the raw string input.
	 * Recommended to not use this as there could be return errors.
	 * @see ExpressionData#getSingle(Event, Class, int)
	 * @see ExpressionData#getExpression(Class, int)
	 * 
	 * @return Expression<?>
	*/
	@Nullable
	public Expression<?> getRaw(String input) {
		return expressions[syntax.get(input)];
	}
	
	/**
	 * Get an expression.
	 * 
	 * @param index Grabs the expression in the calling storage index
	 * @return Expression<?>
	*/
	@Nullable
	public Expression<?> get(int index) {
		if (expressions[index] == null) return null;
		return expressions[index];
	}
	
	/**
	 * Get an array of all expressions.
	 * 
	 * @return Expression<?>[]
	*/
	@Nullable
	public Expression<?>[] getExpressions() {
		return expressions;
	}
	
	/**
	 * Get a map containing all expressions and the index number of that expression.
	 * 
	 * @return Map<String, Integer>
	*/
	@Nullable
	public Map<String, Integer> getExpressionsMap() {
		return syntax;
	}
	
	/**
	 * Grabs a {@link Expression} from the data with a given index.
	 * 
	 * @param type The type's class you want to be returned
	 * @param index Grabs the expression in the calling storage index
	 * @retrun Expression<?>
	*/
	@Nullable
	public <T> Expression<?> getExpression(Class<T> type, int index) {
		return expressions[syntax.get(type.getSimpleName().toLowerCase() + index)];
	}
	
	/**
	 * Grabs the first expression in the syntax.
	 * 
	 * @param event The event
	 * @return Object
	*/
	@Nullable
	public Object getFirst(Event event) {
		return (expressions != null && expressions.length > 0) ? expressions[0].getSingle(event) : null;
	}
	
	/**
	 * Grabs all of the values of the first expression in the syntax.
	 * 
	 * @param event The event
	 * @return Object
	*/
	@Nullable
	public Object getAllOfFirst(Event event) {
		return (expressions != null && expressions.length > 0) ? expressions[0].getArray(event) : null;
	}

	/**
	 * Grabs the single value of an expression in the data index.
	 * See {@link ExpressionData#getAll(Event, Class, int)} for an example and more information.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @param index Grabs the value of which expression in the storage
	 * @see ExpressionData#getAll(Event, Class, int)
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getSingle(Event event, Class<T> type, int index) {
		if (getExpression(type, index) == null) return null;
		return (syntax.containsKey(type.getSimpleName().toLowerCase() + index)) ? (T) getExpression(type, index).getSingle(event) : null;
	}
	
	/**
	 * Grabs the single value of the first expression.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @return T
	*/
	public <T> T getSingle(Event event, Class<T> type) {
		return getSingle(event, type, 0);
	}
	
	/**
	 * Grabs a value of an expression as an integer.
	 * 
	 * @param event The event
	 * @param index Grabs the value of which expression in the storage
	 * @return T
	*/
	public Integer getInt(Event event, int index) {
		return getSingle(event, Number.class, index).intValue();
	}
	
	/**
	 * Grabs a value of the first expression as an integer.
	 * 
	 * @param event The event
	 * @return T
	*/
	public Integer getInt(Event event) {
		return getInt(event, 0);
	}
	
	/**
	 * Same as {@link ExpressionData#getAll(Event, Class)} just returning in a {@link List}
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @see ExpressionData#getAll(Event, Class)
	*/
	public <T> List<T> getList(Event event, Class<T> type) {
		return Arrays.asList(getAll(event, type));
	}
	
	/**
	 * Same as {@link ExpressionData#getAll(Event, Class, int)} just returning in a {@link List}
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @param index Grabs the values of which expression in the storage
	 * @see ExpressionData#getAll(Event, Class, int)
	*/
	public <T> List<T> getList(Event event, Class<T> type, int index) {
		return Arrays.asList(getAll(event, type, index));
	}
	
	/**
	 * Grabs all values of an expression in the data index. E.G: for the syntax: 'the %players% have %itemstacks%' it would be:
	 * 
	 * getAll(event, Player.class, 0);
	 * getAll(event, ItemStack.class, 0);
	 * 
	 * This is because there is only one {@link Player} and {@link ItemStack} type expression, respectively. If there were more exact match expressions you would use 0 or 1 to
	 * Distinguish between the two, if there were three it would be 2 to grab it and so on.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @param index Grabs the values of which expression in the storage
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T[] getAll(Event event, Class<T> type, int index) {
		return (syntax.containsKey(type.getSimpleName().toLowerCase() + index)) ? (T[]) getExpression(type, index).getArray(event) : null;
	}
	
	/**
	 * Grabs all values of the first expression.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @see ExpressionData#getAll(Event, Class, int)
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T[] getAll(Event event, Class<T> type) {
		return (syntax.containsKey(type.getSimpleName().toLowerCase() + 0)) ? (T[]) getExpression(type, 0).getArray(event) : null;
	}
	
	/**
	 * Grabs the size of the values in an expression of the array index.
	 * 
	 * @param event The event.
	 * @param type The type's class you want to be returned.
	 * @param index The index of the expression array to check.e
	*/
	@Nullable
	public <T> int getSize(Event event, Class<T> type, int index) {
		return (syntax.containsKey(type.getSimpleName().toLowerCase() + index)) ? getExpression(type, index).getArray(event).length : null;
	}
	
	/**
	 * Grabs the size of the values in the first expression.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	*/
	@Nullable
	public <T> int getSize(Event event, Class<T> type) {
		return (syntax.containsKey(type.getSimpleName().toLowerCase() + 0)) ? getExpression(type, 0).getArray(event).length : null;
	}
	
	/**
	 * Get a map containing all expressions and the single values in Object form.
	 * 
	 * @param event The event
	 * @return Map<Expression<?>, Object>
	*/
	@Nullable
	public <T> Map<Expression<?>, Object> getMap(Event event) {
		Map<Expression<?>, Object> data = new HashMap<Expression<?>, Object>();
		Arrays.asList(expressions).forEach(expression -> data.put(expression, expression.getSingle(event)));
		return (data.isEmpty()) ? null : data;
	}
	
	/**
	 * Get a map containing all expressions and the multiple values in Object form.
	 * 
	 * @param event The event
	 * @return Map<Expression<?>, Object[]>
	*/
	@Nullable
	public <T> Map<Expression<?>, Object[]> getAllMap(Event event) {
		Map<Expression<?>, Object[]> data = new HashMap<Expression<?>, Object[]>();
		Arrays.asList(expressions).forEach(expression -> data.put(expression, expression.getArray(event)));
		return (data.isEmpty()) ? null : data;
	}
	
	/**
	 * Get a map containing all expressions and the single values in an assigned type.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @return Map<Expression<?>, T>
	*/
	/*@Nullable
	public <T> Map<Expression<?>, T> getMapOf(Event event, Class<T> type) {
		Map<Expression<?>, T> data = new HashMap<Expression<?>, T>();
		syntax.forEach((k,v)->{if (k.contains(type.getSimpleName().toLowerCase())) data.put(getExpression(type, v), getSingle(event, type, v));});
		return (data.isEmpty()) ? null : data;
	}*/
	
	/**
	 * Get a map containing all expressions and the multiple values in an assigned type.
	 * 
	 * @param event The event
	 * @param type The type's class you want to be returned
	 * @return Map<Expression<?>, T[]>
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> Map<Expression<?>, T[]> getAllMapOf(Event event, Class<T>... types) {
		int i = 0;
		Map<Expression<?>, T[]> data = new HashMap<Expression<?>, T[]>();
		for (Class<T> type : types) {
			for (String string : syntax.keySet()) {
				if (string.contains(type.getSimpleName().toLowerCase()) && getExpression(type, i) != null) {
					data.put(getExpression(type, i), getAll(event, type, i));
				}
			}
			i++;
		}
		return (data.isEmpty()) ? null : data;
	}
	
	public String toString(Event event, boolean debug) {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		for (Expression<?> expression : expressions)
			builder.append(expression.toString(event, debug));
		return builder.toString();
	}
}
