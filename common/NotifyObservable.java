/*
 * NotifyObservable.java
 *
 * Created on 19.02.2010, 10:10:29
 */
package common;

import java.util.*;

/**
 * Класс для отправки пользовательских событий наблюдателям.
 *
 * @see NotifyObject
 * @see Observable
 * @see Observer
 * @author imp (дата крайней правки: 2010-02-19)
 */
public class NotifyObservable extends Observable
{

	/**
	 * Пустой конструктор.
	 */
	public NotifyObservable()
	{
	}

	/**
	 * Ставит флаг, что было получено событие.
	 */
	public void modify()
	{
		setChanged();
	}

	public void notify(String eventName)
	{
		notify(eventName, null, 0);
	}
	/**
	 * Функция для отправки генерируемого события зарегестрированным наблюдателям.
	 *
	 * @param event			Имя генерируемого события
	 * @param param			Дополнительный параметр события. В нём может передаваться event компонента, генерирующего это событие.
	 */
	public void notify(String eventName, Object param)
	{
		notify(eventName, param, 0);
	}

	public void notify(String eventName, Object param, int status)
	{
		modify();
		notifyObservers(new NotifyObject(eventName, param, status));
	}
}
