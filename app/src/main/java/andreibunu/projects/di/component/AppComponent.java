package andreibunu.projects.di.component;


import javax.inject.Singleton;

import andreibunu.projects.App;
import andreibunu.projects.di.modules.ApiModule;
import andreibunu.projects.di.modules.AppModule;
import andreibunu.projects.di.modules.ClientModule;
import andreibunu.projects.di.modules.DatabaseModule;
import andreibunu.projects.ui.friends.FriendFragment;
import andreibunu.projects.ui.gallery.GalleryTypeFragment;
import andreibunu.projects.ui.login.LoginFragment;
import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class, DatabaseModule.class, ClientModule.class, AppModule.class})
public interface AppComponent {
    void inject(App app);

    void inject(GalleryTypeFragment galleryTypeFragment);

    void inject(LoginFragment loginFragment);

    void inject(FriendFragment friendFragment);
}
