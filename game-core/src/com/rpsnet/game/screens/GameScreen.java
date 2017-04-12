package com.rpsnet.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rpsnet.game.GameClient;
import com.rpsnet.game.RPSNet;
import com.rpsnet.game.actors.GameActors;
import com.rpsnet.network.GameChoice;
import com.rpsnet.network.Packets;

public class GameScreen implements NetScreen
{
    RPSNet game;
    GameClient gameClient;
    Stage stage;
    SpriteBatch batch;
    GameActors gameActors;

    private Texture backgroundImg;
    private Texture rockTex;
    private Texture paperTex;
    private Texture scissorsTex;

    private Sprite playerChoice;
    private Sprite opponentChoice;

    private int gameID;

    public GameScreen(RPSNet g, GameClient client, Packets.GameSetup setupInfo)
    {
        //Setup the stage
        game = g;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        //Setup the actors
        gameActors = new GameActors(this, setupInfo);
        stage.addActor(gameActors.getPlayerInfoWidgets());
        stage.addActor(gameActors.getOpponentInfoWidgets());
        stage.addActor(gameActors.getChoiceWidgets());

        //Assign SpriteBatch, textures and sprites
        batch = new SpriteBatch();
        backgroundImg = new Texture("background.jpg");
        rockTex = new Texture("rock.png");
        paperTex = new Texture("paper.png");
        scissorsTex = new Texture("scissors.png");
        opponentChoice = new Sprite();
        playerChoice = new Sprite();
        playerChoice.setPosition((Gdx.graphics.getWidth() / 4) - (opponentChoice.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (opponentChoice.getHeight() / 2));
        opponentChoice.setPosition(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 4) - (opponentChoice.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (opponentChoice.getHeight() / 2));

        //Assign GameClient and gameID
        gameClient = client;
        gameClient.setCurrentScreen(this);
        gameID = setupInfo.gameID;

        Gdx.gl.glClearColor(1, 0, 1, 1);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundImg,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(playerChoice.getTexture() != null)
            playerChoice.draw(batch);
        if(opponentChoice.getTexture() != null)
            opponentChoice.draw(batch);
        batch.end();

        stage.act();
        stage.draw();
    }

    /**
     * Called when the user has made a choice to send to the server
     * @param choice The choice that the palyer has made
     */
    public void makeChoice(GameChoice choice)
    {
        gameActors.hideChoiceWidgets();
        gameClient.makeChoice(choice);
        switch (choice)
        {
            case ROCK:
                playerChoice.setTexture(rockTex);
                break;
            case PAPER:
                playerChoice.setTexture(paperTex);
                break;
            case SCISSORS:
                playerChoice.setTexture(scissorsTex);
                break;
        }
    }

    /**
     * Called when both players have made their choice and the server has sent a responce
     * @param result The result of the current round
     */
    public void updateGame(Packets.RoundResult result)
    {
        gameActors.nextRound(result.playerScore, result.opponentScore);
    }

    /**
     * Called when the opponent has chosen but the player hasn't
     */
    public void opponentChosen(GameChoice choice)
    {
        gameActors.setOpponentStatusText("Chosen", Color.GREEN);
        switch (choice)
        {
            case ROCK:
                opponentChoice.setTexture(rockTex);
                break;
            case PAPER:
                opponentChoice.setTexture(paperTex);
                break;
            case SCISSORS:
                opponentChoice.setTexture(scissorsTex);
                break;
        }
    }

    public void updateConnectionInfo(boolean connected)
    {
        //TEMP
        System.out.println("Connected: " + connected);
    }

    public void displayErrorMessage(String message)
    {
        //TEMP
        System.out.println(message);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        batch.dispose();
        backgroundImg.dispose();
        rockTex.dispose();
        paperTex.dispose();
        scissorsTex.dispose();
    }

    public int getGameID()
    {
        return gameID;
    }

}
