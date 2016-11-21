package org.eclipse.nebula.widgets.oscilloscope.multichannel;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public interface IOscilloscopeDispatcher {

	/**
	 * This set of values will draw a figure that is similar to the heart beat
	 * that you see on hospital monitors.
	 */
	public static final int[] HEARTBEAT = new int[] { 2, 10, 2, -16, 16, 44, 49, 44, 32, 14, -16, -38, -49, -47, -32,
			-10, 8, 6, 6, -2, 6, 4, 2, 0, 0, 6, 8, 6 };


	/**
	 * This method will get the animation going. It will create a runnable that
	 * will be dispatched to the user interface thread. The runnable increments
	 * a counter that leads to the {@link #getPulse()} value and if this is
	 * reached then the counter is reset. The counter is passed to the hook
	 * methods so that they can prepare for the next pulse.
	 * <p/>
	 * After the hook methods are called, the runnable is placed in the user
	 * interface thread with a timer of {@link #getDelayLoop()} milliseconds.
	 * However, if the delay loop is set to 1, it will dispatch using
	 * {@link Display#asyncExec(Runnable)} for maximum speed.
	 * <p/>
	 * This method is not meant to be overridden, override {@link #init()}
	 * {@link #hookBeforeDraw(Oscilloscope, int)},
	 * {@link #hookAfterDraw(Oscilloscope, int)} and
	 * {@link #hookPulse(Oscilloscope, int)}.
	 * 
	 * 
	 */
	void dispatch();

	/**
	 * Is used to get the color of the foreground when the thing that the scope
	 * is measuring is still alive. The aliveness of the thing that is being
	 * measured is returned by the {@link #isServiceActive()} method. The result
	 * of this method will be used in the
	 * {@link Oscilloscope#setForeground(Color)} method.
	 * 
	 * @return the system color green. Override if you want to control the
	 *         foreground color.
	 * 
	 * @see #getInactiveForegoundColor()
	 * @see Oscilloscope#setForeground(Color)
	 */
	Color getActiveForegoundColor();

	/**
	 * Override this to return the background image for the scope.
	 * 
	 * @return the image stored in {@link #BACKGROUND_MONITOR}. Override to
	 *         supply your own Image.
	 */
	Image getBackgroundImage();

	/**
	 * Override this to set the offset of the scope line in percentages where
	 * 100 is the top of the widget and 0 is the bottom.
	 * 
	 * @return {@link Oscilloscope#BASE_CENTER} which positions in the center.
	 *         Override for other values.
	 */
	int getBaseOffset();

	/**
	 * Tests if the tail must fade.
	 * 
	 * @return this default implementation returns true
	 * @see Oscilloscope#setTailFade(int, int)
	 * @see #getTailSize()
	 */
	boolean getFade();

	/**
	 * This method returns the {@link Oscilloscope}.
	 * 
	 * @return the oscilloscope
	 */
	Oscilloscope getOscilloscope();

	/**
	 * This method sets the {@link Oscilloscope}.
	 * 
	 * @param scope
	 */
	void setOscilloscope(Oscilloscope scope);

	/**
	 * Override this to set the number of steps that is calculated before it is
	 * actually drawn on the display. This will make the graphic look more jumpy
	 * for slower/higher delay rates but you can win speed at faster/lower delay
	 * rates. There will be {@link #getProgression()} values consumed so make
	 * sure that the value stack contains enough entries.
	 * <p/>
	 * If the {@link #getDelayLoop()} is 10 and the {@link #getPulse()} is 1 and
	 * the {@link #getProgression()} is 5 then every 10 milliseconds the graph
	 * will have progressed 5 pixels. If you want to avoid gaps in your graph,
	 * you need to input 5 values every time you reach
	 * {@link #hookSetValues(int)}. If the {@link #getPulse()} is 3, you need to
	 * input 15 values for a gapless graph. Alternatively, you can implement a
	 * stack listener in the scope to let it call you in case it runs out of
	 * values.
	 * 
	 * @return 1. Override and increment for more speed. Must be higher then
	 *         zero.
	 */
	int getProgression();

	/**
	 * Returns the channel that this scope is the dispatcher for. Please note
	 * that you do not really need one dispatcher per channel.
	 * 
	 * @return the channel for this dispatcher. Can be -1 if not set.
	 */
	int getChannel();

	/**
	 * Is called just after the widget is redrawn every {@link #getDelayLoop()}
	 * milliseconds. The pulse counter will be set to zero when it reaches
	 * {@link #getPulse()}.
	 * 
	 * @param oscilloscope
	 * @param counter
	 */
	void hookAfterDraw(Oscilloscope oscilloscope, int counter);

	/**
	 * Is called just before the widget is redrawn every {@link #getDelayLoop()}
	 * milliseconds. It will also call the {@link #hookChangeAttributes()}
	 * method if the number of times this method is called matches the
	 * {@link #getPulse()} value. The pulse counter will be set to zero when it
	 * reaches {@link #getPulse()}.
	 * <p/>
	 * If you override this method, don't forget to call
	 * {@link #hookChangeAttributes()} every now and then.
	 * 
	 * @param oscilloscope
	 * @param counter
	 */
	void hookBeforeDraw(Oscilloscope oscilloscope, int counter);

	/**
	 * This method sets the values in the scope by calling the individual value
	 * methods in the dispatcher. Be aware that this method actually calls some
	 * candy so you might want to call super.
	 */
	void hookChangeAttributes();

	/**
	 * This method is called every time the dispatcher reaches the getPulse()
	 * counter. This method plays the active or inactive sound if so required.
	 * If you do not want the sounds to play, either disable sounds by not
	 * overriding the {@link #isSoundRequired()} method or override this method.
	 * 
	 * @param oscilloscope
	 * @param pulse
	 */
	void hookPulse(Oscilloscope oscilloscope, int pulse);

	/**
	 * This method will be called every {@link #getPulse()} times the scope is
	 * redrawn which will occur every {@link #getDelayLoop()} milliseconds (if
	 * your hardware is capable of doing so). The scope will progress one pixel
	 * every {@link #getDelayLoop()} milliseconds and will draw the next value
	 * from the queue of the scope. If the scope is out of values it will
	 * progress one pixel without a value (draw a pixel at his center).
	 * <p/>
	 * If the delay loop is 10 and the pulse is 20, you have an opportunity to
	 * set a value in the scope every 200 milliseconds. In this time the scope
	 * will have progressed 20 pixels. If you supply 10 values by calling the
	 * setValue(int) 10 times or if you call the setValues(int[]) with 10 ints
	 * then you will see 10 pixels of movement and a straight line of 10 pixels.
	 * <p/>
	 * If the setPulse method is not overridden or if you supply
	 * {@link #NO_PULSE} then this method will not be called unless you override
	 * the dispatch method (not recommended). To still set values in the scope
	 * you can set a stack listener in the widget that will be called when there
	 * are no more values in the stack. Alternatively you can set the return
	 * value of {@link #getPulse()} to 1 so you have the opportunity to provide
	 * a value every cycle.
	 * 
	 * @param pulse
	 * @see Oscilloscope#setValue(int)
	 * @see Oscilloscope#setValues(int[])
	 * @see Oscilloscope#addStackListener(OscilloscopeStackAdapter)
	 */
	void hookSetValues(int pulse);

	/**
	 * Will be called only once.
	 */
	void init();

	/**
	 * Indicates if the value that comes in from the scope is a percentage
	 * rather than an absolute value.
	 * 
	 * @return the default implementation returns true
	 * @see Oscilloscope#setPercentage(int, boolean)
	 */
	boolean isPercentage();

	/**
	 * @return this default implementation returns false
	 * @see Oscilloscope#setSteady(int, boolean, int)
	 */
	boolean isSteady();

	/**
	 * @return this default implementation returns false
	 * @see Oscilloscope#setConnect(int, boolean)
	 */
	boolean mustConnect();

}