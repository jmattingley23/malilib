package fi.dy.masa.malilib.overlay.message;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.config.option.BooleanContainingConfig;
import fi.dy.masa.malilib.config.value.ScreenLocation;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageUtils
{
    protected static final Pattern PATTERN_TIME_MSG = Pattern.compile("time=(?<time>[0-9]+);(?<msg>.*)");

    public static final String CUSTOM_ACTION_BAR_MARKER = "malilib_actionbar";


    public static MessageRendererWidget getMessageRendererWidget(@Nullable final ScreenLocation location,
                                                                 @Nullable final String marker)
    {
        MessageRendererWidget widget = findInfoWidget(MessageRendererWidget.class, location, marker);

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.CENTER);
            widget.setZ(300f);
            widget.setWidth(300);
            widget.setRenderAboveScreen(true);

            if (marker != null)
            {
                widget.getMarkerManager().addMarker(marker);
            }

            Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        }

        return widget;
    }

    public static MessageRendererWidget getCustomActionBarMessageRenderer()
    {
        MessageRendererWidget widget = findInfoWidget(MessageRendererWidget.class, null, CUSTOM_ACTION_BAR_MARKER);

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(ScreenLocation.BOTTOM_CENTER);
            widget.getMarkerManager().addMarker(CUSTOM_ACTION_BAR_MARKER);
            widget.setZ(300f);
            widget.getMargin().setBottom(50);
            widget.setMessageGap(2);
            widget.setAutomaticWidth(true);
            widget.setName(StringUtils.translate("malilib.label.misc.default_custom_hotbar_message_renderer"));
            widget.setMaxMessages(MaLiLibConfigs.Generic.CUSTOM_HOTBAR_MESSAGE_LIMIT.getIntegerValue());
            Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        }

        return widget;
    }

    public static ToastRendererWidget getToastRendererWidget(@Nullable final ScreenLocation location,
                                                             @Nullable final String marker)
    {
        ToastRendererWidget widget = findInfoWidget(ToastRendererWidget.class, location, marker);

        if (widget == null)
        {
            widget = new ToastRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.TOP_RIGHT);
            widget.setZ(310f);

            if (marker != null)
            {
                widget.getMarkerManager().addMarker(marker);
            }

            Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        }

        return widget;
    }

    @Nullable
    public static <T extends InfoRendererWidget> T findInfoWidget(Class<T> widgetClass,
                                                                  @Nullable final ScreenLocation location,
                                                                  @Nullable final String marker)
    {
        Predicate<T> predicateLocation = location != null ? w -> w.getScreenLocation() == location : w -> true;
        Predicate<T> predicateMarker = w -> w.getMarkerManager().matchesMarker(marker);
        Predicate<T> filter = predicateLocation.and(predicateMarker);
        return Registry.INFO_OVERLAY.findWidget(widgetClass, filter);
    }

    /**
     * Prints the message to the MessageOutput set in the given Hotkey's advanced KeyBindSetting
     */
    public static void printMessage(Hotkey hotkey, String translationKey, Object... args)
    {
        MessageOutput output = hotkey.getKeyBind().getSettings().getMessageType();
        printMessage(output, translationKey, args);
    }

    public static void printMessage(MessageOutput output, String translationKey, Object... args)
    {
        MessageDispatcher.generic().type(output).translate(translationKey, args);
    }

    public static void printCustomActionbarMessage(String translationKey, Object... args)
    {
        MessageDispatcher.generic(5000).fadeOut(500).customHotbar().translate(translationKey, args);
    }

    public static void printBooleanConfigToggleMessage(MessageOutput type,
                                                       BooleanContainingConfig<?> config,
                                                       @Nullable BooleanConfigMessageFactory messageFactory)
    {
        String msg = MessageHelpers.getBooleanConfigToggleMessage(config, messageFactory);

        if (org.apache.commons.lang3.StringUtils.isBlank(msg) == false)
        {
            MessageDispatcher.generic(5000).type(type).send(msg);
        }
    }

    public static ActionResult addMessageAction(ActionContext ctx, String msg)
    {
        return addMessageAction(MessageOutput.MESSAGE_OVERLAY, msg);
    }

    public static ActionResult addToastAction(ActionContext ctx, String msg)
    {
        return addMessageAction(MessageOutput.TOAST, msg);
    }

    public static ActionResult addMessageAction(MessageOutput type, String msg)
    {
        int displayTimeMs = 5000;
        Matcher matcher = PATTERN_TIME_MSG.matcher(msg);

        try
        {
            if (matcher.matches())
            {
                displayTimeMs = Integer.parseInt(matcher.group("time"));
                msg = matcher.group("msg");
            }
        }
        catch (Exception ignore) {}

        MessageDispatcher.generic(displayTimeMs).type(type).send(msg);

        return ActionResult.SUCCESS;
    }

}
