package ir.ap.view;

public enum Menu {
    LOGIN(new LoginMenuView()),
    MAIN(new MainMenuView()),
    GAME(new GameMenuView()),
    PROFILE(new ProfileMenuView());

    private final AbstractMenuView menuView;

    Menu(AbstractMenuView menuView) {
        this.menuView = menuView;
    }

    public static Menu getMenuByName(String menuName) {
        switch (menuName.trim().toUpperCase()) {
            case "LOGIN":
                return LOGIN;
            case "MAIN":
                return MAIN;
            case "GAME":
                return GAME;
            case "PROFILE":
                return PROFILE;
            default:
                return null;
        }
    }

    public AbstractMenuView getMenuView() {
        return menuView;
    }

    public Menu runCommand() {
        return menuView.runCommand();
    }
}
