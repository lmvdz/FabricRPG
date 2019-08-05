package io.github.xeonpowder.fabric.rpg.gui.screen;

import java.awt.Color;
import java.util.List;

import io.github.TUSK__3.panI18n.FormattingEngine;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.xeonpowder.fabric.rpg.gui.panel.LocationPanel;
import io.github.xeonpowder.fabric.rpg.gui.panel.LocationsPanel;
import io.github.xeonpowder.fabric.rpg.portalnetwork.LevelPropertiesPortalNetwork;
import io.github.xeonpowder.fabric.rpg.portalnetwork.PlayerPortalNetwork;
import io.github.xeonpowder.fabric.rpg.portalnetwork.PortalNetwork;
import io.github.xeonpowder.fabric.rpg.portalnetwork.WorldPortalNetwork;
import io.github.xeonpowder.fabric.rpg.portalnetwork.node.PortalNetworkNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

/**
 * PortalNetworkScreen
 */
public class PortalNetworkLightweightGuiDescription extends LightweightGuiDescription {
        public static PortalNetwork<PlayerEntity> playerPortalNetwork;
        public static PortalNetwork<World> worldPortalNetwork;
        public static PortalNetwork<LevelProperties> levelPortalNetwork;

        public PortalNetworkLightweightGuiDescription(World world, PlayerEntity player) {
                super();
                if (world.isClient) {
                        playerPortalNetwork = PlayerPortalNetwork.PLAYER_ENTITY_PORTAL_NETWORK.get(player)
                                        .getPortalNetwork();
                        worldPortalNetwork = WorldPortalNetwork.WORLD_PORTAL_NETWORK.get(world).getPortalNetwork();
                        levelPortalNetwork = LevelPropertiesPortalNetwork.LEVEL_PROPERTIES_PORTAL_NETWORK
                                        .get(world.getLevelProperties()).getPortalNetwork();

                        WPlainPanel rootPanel = new WPlainPanel();
                        setRootPanel(rootPanel);
                        // get window
                        Window window = MinecraftClient.getInstance().window;
                        // set root panel size
                        rootPanel.setSize(((int) (window.getScaledWidth() * .66)),
                                        ((int) (window.getScaledHeight() * .66)));
                        // set root panel location
                        rootPanel.setLocation(((int) (window.getScaledWidth() * .17)),
                                        ((int) (window.getScaledHeight() * .17)));
                        // set root panel background painter
                        rootPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
                        // paint the background from top left to bottom right of root panel
                        rootPanel.paintBackground(0, rootPanel.getHeight());

                        // setup locationsPanel -- will contain a list of PlainPanels
                        LocationsPanel locationsPanel = new LocationsPanel();
                        // WPlainPanel locationsPanel = new WPlainPanel();
                        // setup default locationsPanel painter
                        locationsPanel.setSize(((int) (rootPanel.getWidth() * .8)),
                                        ((int) (rootPanel.getHeight() * .8)));

                        List<PortalNetworkNode> playerNodeHistoryList = playerPortalNetwork.getNodes();
                        List<PortalNetworkNode> worldNodeHistoryList = worldPortalNetwork.getNodes();
                        List<PortalNetworkNode> levelPortalNetworkNodes = levelPortalNetwork.getNodes();

                        worldNodeHistoryList.addAll(playerNodeHistoryList);
                        if (worldNodeHistoryList.size() != 0) {
                                worldNodeHistoryList.forEach(portalNetworkNode -> {
                                        Position nodePosition = portalNetworkNode.getPosition();
                                        LocationPanel locationPanel = new LocationPanel() {
                                                @Override
                                                public void onClick(int x, int y, int button) {
                                                        onClick(player, nodePosition);
                                                }
                                        };
                                        // setup default painter
                                        locationPanel.setBackgroundPainter((left, top, panel) -> {
                                                ScreenDrawing.drawGuiPanel(left - 8, top - 8, panel.getWidth() + 14,
                                                                panel.getHeight() + 14, 0xFF2F2F2F);
                                        });
                                        // add title of node
                                        WLabel locationLabel = new WLabel(
                                                        new LiteralText(FormattingEngine.replaceColorCodeEnumInString(
                                                                        portalNetworkNode.getTranslatedText()
                                                                                        .asString())),
                                                        WLabel.DEFAULT_DARKMODE_TEXT_COLOR);
                                        // add location of node
                                        WLabel locationPosition = new WLabel(new LiteralText(
                                                        FormattingEngine.replaceColorCodeEnumInString(portalNetworkNode
                                                                        .getPositionAsStringForPortalNetworkGui())),
                                                        WLabel.DEFAULT_DARKMODE_TEXT_COLOR);
                                        // add cost of teleportation to the node
                                        int teleportCost = locationPanel.calculateRequiredCurrency(player.getPos(),
                                                        nodePosition);
                                        WLabel locationCost = new WLabel(new LiteralText(
                                                        FormattingEngine.replaceColorCodeEnumInString("Cost: "
                                                                        + ((teleportCost > 0) ? (teleportCost
                                                                                        + " Portal Flower") : "Free")
                                                                        + ((teleportCost > 1) ? "s." : "."))),
                                                        WLabel.DEFAULT_DARKMODE_TEXT_COLOR);
                                        // add the locationLabel text
                                        locationPanel.add(locationLabel, ((int) (locationsPanel.getWidth() * .025)),
                                                        ((int) (locationPanel.getHeight() * .1)));
                                        // add the locationPosition text
                                        locationPanel.add(locationPosition, ((int) (locationsPanel.getWidth() * .025)),
                                                        ((int) (locationPanel.getHeight() * .5)));
                                        // add the locationCost text
                                        locationPanel.add(locationCost, ((int) (locationsPanel.getWidth() * .025)),
                                                        ((int) (locationPanel.getHeight() * 1.1)));
                                        // add the locationPanel to the locationsPanel list
                                        // setup x,y location of locationPanel relative to locationsPanel
                                        // setup size of the locationPanel
                                        locationsPanel.add(locationPanel, (int) (locationsPanel.getWidth() * .1),
                                                        (worldNodeHistoryList.indexOf(portalNetworkNode) + 1)
                                                                        * locationPanel.getHeight(),
                                                        ((int) (locationsPanel.getWidth() * .8)),
                                                        ((int) (rootPanel.getHeight() * .2)));

                                        // paint background for the first time after settings the size of the panel
                                        locationPanel.paintBackground(0, locationPanel.getHeight());
                                });
                        }
                        // query teleport network using hashCode of past query -- teleport network
                        // compares hashCode to see if new information needs to be queried.
                        WPlainPanel titlePanel = new WPlainPanel();
                        // Title text.
                        LiteralText titleLabelText = new LiteralText(FormattingEngine.replaceColorCodeEnumInString(
                                        (new TranslatableText("gui.fabric_rpg.portal_network").asString())));
                        // title label
                        WLabel titleLabel = new WLabel(titleLabelText, WLabel.DEFAULT_TEXT_COLOR);
                        // add title label to title panel
                        titlePanel.add(titleLabel,
                                        ((rootPanel.getWidth() / 2) - MinecraftClient.getInstance().textRenderer
                                                        .getStringWidth(titleLabelText.asString()) / 2),
                                        (int) (titlePanel.getHeight() * .5));
                        rootPanel.add(titlePanel, 0, 0, rootPanel.getWidth(), (int) (rootPanel.getHeight() * .15));
                        rootPanel.add(locationsPanel, ((int) (rootPanel.getWidth() * .1)), // offset left 10% of
                                                                                           // rootPanel width
                                        ((int) (titlePanel.getHeight() * 1.1)) + ((int) (titlePanel.getHeight() * .5)), // offset
                                                                                                                        // top
                                                                                                                        // based
                                                                                                                        // on
                                                                                                                        // titlePanel
                                                                                                                        // Height
                                        ((int) (rootPanel.getWidth() * .8)), // width should be 80% of rootPanel width
                                                                             // to allow for space on
                                                                             // right and left to equal 10%
                                        ((int) (rootPanel.getHeight() * .8)) // height will be 80% of root Panel height
                        );
                        locationsPanel.paintBackground(0, locationsPanel.getHeight());
                        rootPanel.validate(this);
                }
        }

        public void queryServerForPlayerPortalNetwork() {

        }
}