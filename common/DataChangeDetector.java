/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Класс отслеживающий любые изменения в форме ввода. Поддерживается до 63*64 = 4032 объектов.
 * В данный момент поддерживаются: FilterTextbox, CustomDatePicker, JTextArea, TTComboBox, JCheckBox, JRadioButton, TreeTable
 *
 * !!! Важно !!!
 * При использовании с плагинами, когда плагины меняются на лету - не забываем
 * использовать clearItems() илл removeWatchingItem() - чтобы не оставались висящие ссылки на уже не существующие элементы.
 * В элементах где удаляются вкладки/объекты - также не забываем вызывать removeWatchingItem()
 * !!! Важно !!!
 *
 * @author imp
 */
public class DataChangeDetector implements PropertyChangeListener, DocumentListener, ItemListener,ChangeListener
{

    /**
     * Событие уведомляющее, что в отслеживаемых объектах изменились какие-то данные
     */
    public static final String EVENT_DATA_CHANGED = "dcd_changed";

    private NotifyObservable observable;


    private class WatchItem {
        public Object item, value;

        public WatchItem()
        {
            item = value = null;
        }
        public WatchItem(Object it)
        {
            item = it;
            value = null;
        }
        public WatchItem(Object it, Object val)
        {
            item = it;
            value = val;
        }
    }
    private ArrayList<WatchItem> items;
    private boolean watching = false;

    private static final int MAX_SIZE = 63;
    private static final int MAX_BIT_SIZE = 64;
    private static final int MAX_ITEMS_COUNT = MAX_SIZE * MAX_BIT_SIZE;
    private static final long RESERV_MANUL = 63;
    private long[] detection_bits;
    private long detector = 0;
    private MessageDigest md;
    private String name = "";

    /**
     * Конструктор класса
     */
    public DataChangeDetector()
    {
        detection_bits = new long[MAX_SIZE];
        items = new ArrayList<WatchItem>();
        try
        {
            md = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException ex)
        {
            Logger.getLogger(DataChangeDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Указать слушатель событий об изменении данных
     * @param o слушатель
     */
    public void setObserver(Observer o)
    {
        if(observable == null)
        {
            observable = new NotifyObservable();
        }
        observable.addObserver(o);
    }

    private void notify(String event, Object val)
    {
        if(observable != null)
        {
            observable.notify(name + event, val);
        }
    }

    /**
     * Получить имя детектора
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Установить имя детектора. Необходимо при одновременном использовании
     * нескольких детекторов в одном классе. При заданном имени, получаемые классом
     * события EVENT_DATA_CHANGED, будут начинаться с заданного имени детектора.
     * @param name имя
     */
    public void setName(String name)
    {
        this.name = (name == null ? "" : name);
    }

    /**
     * Добавить отслеживаемый объект. В данный момент поддерживаются: FilterTextbox, CustomDatePicker, JTextArea, TTComboBox, JCheckBox, JRadioButton, TreeTable
     * @param item объект/компонент
     * @return true - если объект был успешно добавлен
     */
    public boolean addWatchingItem(Object item)
    {
        if(item == null)
        {
            System.err.println("Нельзя добавлять в отслеживаемые null`овские объекты.");
            return false;
        }
        if(items.size() >= MAX_ITEMS_COUNT)
        {
            System.err.println("Превышено количество отслеживаемых объектов.");
            return false;
        }

        boolean gotcha = false;
        if(item instanceof JTextField)
        {
            JTextField ft = (JTextField) item;
            ft.getDocument().putProperty("parent", ft);
            ft.getDocument().addDocumentListener(this);
            gotcha = true;
        } else if(item instanceof JSpinner)
        {
            JSpinner cdp = (JSpinner) item;
            cdp.addChangeListener(this);
            gotcha = true;
        } else if(item instanceof JTextArea)
        {
            JTextArea jta = (JTextArea) item;
            jta.getDocument().putProperty("parent", jta);
            jta.getDocument().addDocumentListener(this);
            gotcha = true;
        } else if(item instanceof JCheckBox)
        {
            JCheckBox jcb = (JCheckBox) item;
            jcb.addItemListener(this);
            gotcha = true;
        } else if(item instanceof JRadioButton)
        {
            JRadioButton jrb = (JRadioButton) item;
            jrb.addItemListener(this);
            gotcha = true;
        }
        if(gotcha)
        {
            gotcha = false;
            int cnt = 0;
            for(WatchItem wi : items)
            {
                if(wi.item == null)
                {
                    wi.item = item;
                    wi.value = getValue(item);
                    gotcha = true;
                    //System.out.println("replace wi at " + cnt);
                    break;
                }
                cnt++;
            }
            if(!gotcha)
            {
                WatchItem wi = new WatchItem(item, getValue(item));
                items.add(wi);
                gotcha = true;
                //System.out.println("add new wi");
            }
            //System.out.println("wi cnt = " + items.size());
        } else {
            System.err.println("Данный объект не поддерживается : " + item);
        }
        return gotcha;
    }

    /**
     * Автоматически собирает и добавляет в отслеживаемые все элементы на форме
     * @param cmp контейнер с элементами
     */
    public void gatherWatchItems(Component cmp)
    {
        if(cmp instanceof Container)
        {
            for(Component c : ((Container) cmp).getComponents())
            {
                if(c instanceof Container)
                {
                    gatherWatchItems(c);
                } else {
                    addWatchingItem(c);
                }
            }
        }
    }

    private void removeListeners(Object item)
    {
        if(item == null)
        {
            return;
        }

        if(item instanceof JTextField)
        {
            ((JTextField) item).removePropertyChangeListener("value", this);
        } else if(item instanceof JSpinner)
        {
            ((JSpinner) item).removePropertyChangeListener("date", this);
        } else if(item instanceof JCheckBox)
        {
            ((JCheckBox) item).removeItemListener(this);
        } else if(item instanceof JRadioButton)
        {
            ((JRadioButton) item).removeItemListener(this);
        } 
    }

    /**
     * Установить зарезервированный бит ручной регистрации изменений в нужное положение.
     * "Закат солнца вручную" - для сложных компонент, где проще по определённым событиям
     * выставить флаг, что были изменения, чем прописывать всё-всё-всё.
     * @param changed true если нужно считать, что были изменения
     */
    public void setManualChange(boolean changed)
    {
        if(!watching)
        {
            return;
        }
        if(!changed)
        {
            detector &= ~(1L << RESERV_MANUL);
        } else {
            detector |= (1L << RESERV_MANUL);
        }
        notify(EVENT_DATA_CHANGED, detector);
    }

    /**
     * Убрать указанный элемент из списка отслеживаемых
     * @param elem объект, который необходимо убрать из списка
     * @return true если объект был успешно удалён
     */
    public boolean removeWatchingItem(Object elem)
    {
        boolean gotcha = false;
        int cnt = 0;
        for(WatchItem wi : items)
        {
            if(elem == wi.item)
            {
                removeListeners(elem);
                wi.item = null;
                wi.value = null;
                setDetectValue(cnt, true);
                gotcha = true;
                break;
            }
            cnt++;
        }
        if(gotcha)
        {
            notify(EVENT_DATA_CHANGED, detector);
        }
        return gotcha;
    }

    /**
     * Очистить список отслеживаемых объектов. Слежение прекращается автоматически.
     */
    public void clearItems()
    {
        stopWatch();
        for(int idx = 0; idx < MAX_SIZE; detection_bits[idx++] = 0){}
        detector = 0;
        for(WatchItem wi : items)
        {
            removeListeners(wi.item);
        }
        items.clear();
    }

    private Object getValue(Object item)
    {
        Object value = null;
        if(item != null)
        {
            if(item instanceof JTextField)
            {
                value = ((JTextField) item).getText();
            } else if(item instanceof JSpinner)
            {
                value = ((JSpinner) item).getValue();
            } else if(item instanceof JTextArea)
            {
                value = ((JTextArea) item).getText();
            } else if(item instanceof JCheckBox)
            {
                value = ((JCheckBox) item).isSelected();
            } else if(item instanceof JRadioButton)
            {
                value = ((JRadioButton) item).isSelected();
            } 
            if(value == null)
            {
                System.err.println("Null value! Element = " + item);
            }
        }
        return value;
    }

    /**
     * Начать отслеживание изменений. Сравнение ведётся со значениями объектов на момент начала отслеживания.
     */
    public void startWatch()
    {
        for(WatchItem wi : items)
        {
            wi.value = getValue(wi.item);
        }
        for(int idx = 0; idx < MAX_SIZE; detection_bits[idx++] = 0){}
        detector = 0;
        watching = true;
    }

    /**
     * Остановить слежение.
     */
    public void stopWatch()
    {
        watching = false;
    }

    /**
     * Идёт ли в данный момент отслеживание изменений
     * @return true если идёт
     */
    public boolean isWatching()
    {
        return watching;
    }

    /**
     * Проверить вручную - есть ли изменения.
     * @return true - если изменения есть
     */
    public boolean isChanged()
    {
        return detector != 0;
    }

    private void setDetectValue(int idx, boolean ok)
    {
        int group_idx = (idx - 1) / MAX_BIT_SIZE;
        int bit_idx = (idx - 1) % MAX_BIT_SIZE;
        if(ok)
        {
            detection_bits[group_idx] &= ~(1L << bit_idx);
        } else {
            detection_bits[group_idx] |= (1L << bit_idx);
        }
        if(detection_bits[group_idx] == 0)
        {
            detector &= ~(1L << idx);
        } else {
            detector |= (1L << idx);
        }
    }

    private void checkData(Object elem)
    {
        if(!watching)
        {
            return;
        }
        int cnt = 0;
        for(WatchItem wi : items)
        {
            if(wi.item != null && elem == wi.item)
            {
                boolean ok;
                Object value = getValue(elem);
                ok = value.equals(wi.value);
                setDetectValue(cnt, ok);
                break;
            }
            cnt++;
        }
        notify(EVENT_DATA_CHANGED, detector);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        checkData(evt.getSource());
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        checkData(e.getDocument().getProperty("parent"));
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        checkData(e.getDocument().getProperty("parent"));
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        checkData(e.getDocument().getProperty("parent"));
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        checkData(e.getSource());
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        checkData(ce.getSource());
    }

    
}
