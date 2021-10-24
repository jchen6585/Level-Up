package com.mygdx.nextlevel.screens.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

import java.util.ArrayList;

public class AssetSelectionPane extends Tab {
    private String name;
    private ScrollPane scrollPane;
    private VisTable table;
    private ButtonGroup buttonGroup;
    private Table innerTable;

    public AssetSelectionPane(String name, ArrayList list) {
        super(false, false);
        this.name = name;

        // This table holds the content the scroll pane will contain
        innerTable = new Table();
        innerTable.top();

        // We want the scroll pane to move smoothly, and we don't want the bar to disappear
        scrollPane = new ScrollPane(innerTable, VisUI.getSkin());
        scrollPane.setSmoothScrolling(true);
        scrollPane.setFadeScrollBars(false);

        // The button group adds the functionality for selecting objects.
        // For each pane we want exactly 1 object to be selected.
        buttonGroup = new ButtonGroup();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        if(list != null && list.size() > 0) {
            if(list.get(0) instanceof String)
                addFromNameList(list);
            else if (list.get(0) instanceof Texture)
                addFromTextureList(list);
            else
                throw new IllegalArgumentException("Input must be a list of file names or textures");
        }

        table = new VisTable();
        table.add(scrollPane).expand().fillX().align(Align.top);
    }

    protected void addFromNameList(ArrayList<String> names) {
        ArrayList<Texture> textures = new ArrayList<Texture>();

        for(String name : names) {
            Texture tex = new Texture(name);

            textures.add(tex);
        }

        addFromTextureList(textures);
    }

    protected void addFromTextureList(ArrayList<Texture> textures) {
        buttonGroup.clear();

        //Constants for layout formatting
        final float size = 128;
        final float pad = 10.0f;

        // Add the images for each of the resources to the table.
        int i = 0;
        for(Texture tex : textures) {
            TextureRegionDrawable trdNormal = new TextureRegionDrawable(tex);
            Drawable dChecked = trdNormal.tint(new Color(0.2f, 0.2f, 0.5f, 0.5f));

            final ImageButton ib = new ImageButton(trdNormal, dChecked, dChecked);

            TextButton b = new TextButton("test", VisUI.getSkin(), "toggle");

            innerTable.add(ib).expand().fill().padBottom(pad).padTop(pad).size(size);
            buttonGroup.add(ib);

            // A new row every 2 lines
            if(++i % 2 == 0)
                innerTable.row();
        }

        //Select the first object
        buttonGroup.setChecked("test");
    }

    @Override
    public String getTabTitle() {
        return name;
    }

    @Override
    public Table getContentTable() {
        return table;
    }

    public int getSelectionIndex() {
        return buttonGroup.getCheckedIndex();
    }
}