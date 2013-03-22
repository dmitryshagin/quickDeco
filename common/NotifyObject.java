/*
 * NotifyObject.java
 *
 * Created on 19.02.2010, 10:02:19
 */
package common;

/**
 * Класс пользовательского события, используемого классом common.NotifyObservable
 *
 * @see NotifyObservable
 * @see Observable
 * @see Observer
 * @author imp (дата крайней правки: 2010-02-19)
 */
public class NotifyObject
{

	/**
	 * Имя события. Строка идентифицирующая это событие. Желательно уникально.
	 */
	private String eventName;
	/**
	 * Дополнительный параметр. Может быть всё что угодно. К примеру, event 
	 * компонента, который сгенерировал это событие - для последующей передачи
	 * управления этому объекту.
	 */
	private Object param;
	/**
	 * Доп. параметр - статус события
	 */
	private int status;

	/**
	 * Конструктор
	 *
	 * @param eventName		Собственно имя
	 * @param param			Дополнительный параметр
	 * @param status		Статус события
	 */
	public NotifyObject(String eventName, Object param, int status)
	{
		this.eventName = eventName;
		this.param = param;
		this.status = status;
	}

	/**
	 *
	 * @return
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 *
	 * @param eventName
	 */
	public void setEventName(String eventName)
	{
		this.eventName = eventName;
	}

	/**
	 *
	 * @return
	 */
	public Object getParam()
	{
		return param;
	}

	/**
	 *
	 * @param param
	 */
	public void setParam(Object param)
	{
		this.param = param;
	}

	/**
	 *
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 *
	 * @param status
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}
}
