package com.whiuk.philip.mmorpg.client.ui;

import org.apache.log4j.Logger;

import com.whiuk.philip.mmorpg.client.AuthInterface;
import com.whiuk.philip.mmorpg.client.GameClient;
import com.whiuk.philip.mmorpg.client.GameClient.State;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Start screen.
 * @author Philip Whitehouse
 */
public class RegisterScreen implements ScreenController, AuthInterface {
    /**
     *
     */
    @SuppressWarnings("unused")
    private Nifty nifty;
    /**
     *
     */
    private Element textInputUsername;
    /**
     *
     */
    private Element textInputPassword;
    /**
    *
    */
    private Element textInputPasswordConfirm;
    /**
    *
    */
    private Element textInputEmail;
    /**
     *
     */
    private GameClient gameClient;
    /**
     *
     */
    @SuppressWarnings("unused")
    private int ordering;
    /**
     * Number of registration failures.
     */
    private int registrationFailures;
    private Element messageLabel;
    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RegisterScreen.class);
    /**
     * The maximum allowed registration failures.
     */
    private static final int MAX_REGISTRATION_FAILURES = 1;

    /**
     * @param g
     *            Game client
     */
    public RegisterScreen(final GameClient g) {
        this.gameClient = g;
    }

    @Override
    public final void bind(final Nifty newNifty, final Screen screen) {
        this.nifty = newNifty;
        textInputUsername = screen.findElementByName("text_input_username");
        textInputPassword = screen.findElementByName("text_input_password");
        textInputPasswordConfirm = screen
                .findElementByName("text_input_password_confirm");
        textInputEmail = screen.findElementByName("text_input_email");
        messageLabel = screen.findElementByName("message");
        gameClient.setState(State.LOGIN);
    }

    @Override
    public final void onStartScreen() {
        	textInputUsername.addInputHandler(new StandardKeyInputHandler() {
            @Override
            public boolean standardKeyEvent(final NiftyStandardInputEvent inputEvent) {
                    switch (inputEvent) {
                    case SubmitText:
                        textInputPassword.setFocus();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        textInputPassword.addInputHandler(new StandardKeyInputHandler() {
            @Override
            public boolean standardKeyEvent(final NiftyStandardInputEvent inputEvent) {
                switch (inputEvent) {
                    case SubmitText:
                        textInputPasswordConfirm.setFocus();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        textInputPasswordConfirm.addInputHandler(new StandardKeyInputHandler() {
            @Override
            public boolean standardKeyEvent(final NiftyStandardInputEvent inputEvent) {
                switch (inputEvent) {
                    case SubmitText:
                        textInputEmail.setFocus();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        textInputEmail.addInputHandler(new StandardKeyInputHandler() {
            @Override
            public boolean standardKeyEvent(final NiftyStandardInputEvent inputEvent) {
                switch (inputEvent) {
                    case SubmitText:
                        sendRegistrationRequest();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        textInputUsername.setFocus();
    }

    @Override
    public void onEndScreen() {
    }

    /**
     * Sends a login request.
     */
    public final void sendRegistrationRequest() {
        String username = textInputUsername
                .getNiftyControl(TextField.class).getRealText();
        String password = textInputPassword
                .getNiftyControl(TextField.class).getRealText();
        String passwordConfirm = textInputPasswordConfirm
                .getNiftyControl(TextField.class).getRealText();
        String email = textInputPassword
                .getNiftyControl(TextField.class).getRealText();
        if (username.isEmpty()
                || password.isEmpty()
                || passwordConfirm.isEmpty()
                || email.isEmpty()) {
            setMessage("Please complete all required fields");
        } else if (!password.equals(passwordConfirm)) {
            setMessage("Please ensure the passwords match");
        } else if (registrationFailures >= MAX_REGISTRATION_FAILURES) {
            setMessage("Exceeded maximum registration attempts");
        } else if (gameClient.isConnected() && gameClient.hasClientInfo()) {
            gameClient.attemptRegister(
                    username,
                    password,
                    email);
        } else if (!gameClient.isConnected()) {
        		setMessage("Unable to register - not connected");
            LOGGER.info("Client not connected");
        } else if (!gameClient.hasClientInfo()) {
            LOGGER.info("Client info not set");
        } else {
            LOGGER.info("Logic bug");
        }
    }

    @Override
    public void setMessage(final String m) {
        LOGGER.info("Setting message: " + m);
        messageLabel.getNiftyControl(Label.class).setText(m);
    }

    @Override
    public final void registrationFailed(final String reason) {
        registrationFailures++;
        setMessage(reason);
    }

    /**
     *
     */
    public final void login() {
        gameClient.switchToLoginScreen(null);
    }

    @Override
    public final void loginFailed(final String errorMessage) {
        setMessage(errorMessage);
    }

    @Override
    public void handleExtraAuthFailed() {
        // TODO Auto-generated method stub
    }
}
