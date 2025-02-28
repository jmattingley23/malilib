package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.KeybindSettingsScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Identifier;

public class KeybindSettingsWidget extends InteractableWidget
{
    public static final Identifier TEXTURE = new Identifier(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final String keyBindName;
    protected final KeyBind keyBind;

    public KeybindSettingsWidget(KeyBind keyBind, String keyBindName)
    {
        super(20, 20);

        this.keyBind = keyBind;
        this.keyBindName = keyBindName;
        this.setHoverStringProvider("hover_info", this::rebuildHoverStrings);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            KeybindSettingsScreen screen = new KeybindSettingsScreen(this.keyBind, this.keyBindName);
            screen.setParent(GuiUtils.getCurrentScreen());
            BaseScreen.openPopupScreen(screen);

            return true;
        }
        // Reset the settings to defaults on right click
        else if (mouseButton == 1)
        {
            this.keyBind.resetSettingsToDefaults();
            this.updateHoverStrings();
            return true;
        }

        return false;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(TEXTURE);

        KeyBindSettings settings = this.keyBind.getSettings();
        int edgeColor = this.keyBind.areSettingsModified() ? 0xFFFFBB33 : 0xFFFFFFFF;

        ShapeRenderUtils.renderRectangle(x    , y    , z, 20, 20, edgeColor);
        ShapeRenderUtils.renderRectangle(x + 1, y + 1, z, 18, 18, 0xFF000000);

        x += 1;
        y += 1;

        int w = 18;
        int uDiff = 20;
        int u1 = 1 + (settings.getActivateOn().getIconIndex() * uDiff);
        int u2 = 1 + (settings.getAllowExtraKeys() ? uDiff : 0);
        int u3 = 1 + (settings.isOrderSensitive() ? uDiff : 0);
        int u4 = 1 + (settings.isExclusive() ? uDiff : 0);
        // TODO add separate icons for ON_SUCCESS and ON_FAILURE ?
        int u5 = 1 + (settings.getCancelCondition() != CancelCondition.NEVER ? uDiff : 0);
        int u6 = 1 + (settings.getAllowEmpty() ? uDiff : 0);
        int u7 = 61 + (settings.getContext().getIconIndex() * uDiff);

        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u1, 137, w, w); // activate on
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u2, 157, w, w); // allow extra
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u3, 177, w, w); // order sensitive
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u4, 197, w, w); // exclusive
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u5, 217, w, w); // cancel
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u6, 237, w, w); // allow empty
        ShapeRenderUtils.renderTexturedRectangle256(x, y, z, u7, 137, w, w); // context
    }

    protected List<String> rebuildHoverStrings()
    {
        List<String> lines = new ArrayList<>();
        KeyBindSettings settings = this.keyBind.getSettings();
        KeyBindSettings defaultSettings = this.keyBind.getDefaultSettings();

        lines.add(StringUtils.translate("malilib.hover.advanced_keybind_settings.title"));

        this.addOptionText(lines, "malilib.label.keybind_settings.activate_on", settings.getActivateOn(), defaultSettings.getActivateOn(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.context", settings.getContext(), defaultSettings.getContext(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.cancel_further", settings.getCancelCondition(), defaultSettings.getCancelCondition(), this::getDisplayString);

        this.addOptionText(lines, "malilib.label.keybind_settings.allow_extra_keys", settings.getAllowExtraKeys(), defaultSettings.getAllowExtraKeys(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.order_sensitive", settings.isOrderSensitive(), defaultSettings.isOrderSensitive(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.exclusive", settings.isExclusive(), defaultSettings.isExclusive(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.first_only", settings.getFirstOnly(), defaultSettings.getFirstOnly(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.priority", settings.getPriority(), defaultSettings.getPriority(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.allow_empty_keybind", settings.getAllowEmpty(), defaultSettings.getAllowEmpty(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.invert_held", settings.getInvertHeld(), defaultSettings.getInvertHeld(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.toggle", settings.isToggle(), defaultSettings.isToggle(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.show_toast", settings.getShowToast(), defaultSettings.getShowToast(), this::getDisplayString);
        this.addOptionText(lines, "malilib.label.keybind_settings.message_output", settings.getMessageType(), defaultSettings.getMessageType(), this::getDisplayString);

        lines.add("");
        StringUtils.addTranslatedLines(lines, "malilib.label.keybind_settings.tips");

        return lines;
    }

    protected <T> void addOptionText(List<String> lines, String translationKey, T value, T defaultValue, Function<T, String> displayValueFunction)
    {
        boolean modified = value.equals(defaultValue) == false;
        String name = StringUtils.translate(translationKey);
        String valStr = displayValueFunction.apply(value);

        if (modified)
        {
            String key = "malilib.label.keybind_settings.name_and_value.modified";
            String defValStr = displayValueFunction.apply(defaultValue);
            lines.add(StringUtils.translate(key, name, valStr, defValStr));
        }
        else
        {
            String key = "malilib.label.keybind_settings.name_and_value.default";
            lines.add(StringUtils.translate(key, name, valStr));
        }
    }

    protected String getDisplayString(boolean value)
    {
        return StringUtils.translate(value ? "malilib.label.misc.yes.colored" : "malilib.label.misc.no.colored");
    }

    protected String getDisplayString(OptionListConfigValue value)
    {
        return StringUtils.translate("malilib.label.keybind_settings.value.option_list", value.getDisplayName());
    }

    protected String getDisplayString(int value)
    {
        return StringUtils.translate("malilib.label.keybind_settings.value.integer", value);
    }
}
