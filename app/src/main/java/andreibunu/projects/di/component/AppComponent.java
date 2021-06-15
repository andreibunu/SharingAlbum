package andreibunu.projects.di.component;


import javax.inject.Singleton;

import andreibunu.projects.App;
import andreibunu.projects.di.modules.ApiModule;
import andreibunu.projects.di.modules.AppModule;
import andreibunu.projects.di.modules.ClientModule;
import andreibunu.projects.di.modules.DatabaseModule;
import andreibunu.projects.ui.filter.FilterFragment;
import andreibunu.projects.ui.friends.FriendFragment;
import andreibunu.projects.ui.gallery.FriendsGalleryFragment;
import andreibunu.projects.ui.gallery.GalleryTypeFragment;
import andreibunu.projects.ui.image.ImageFragment;
import andreibunu.projects.ui.image.ImageFromFriendFragment;
import andreibunu.projects.ui.login.LoginFragment;
import andreibunu.projects.ui.profile.SettingsFragment;
import dagger.Component;

@Singleton
@Component(modules = {ApiModule.class, DatabaseModule.class, ClientModule.class, AppModule.class})
public interface AppComponent {
    void inject(App app);

    void inject(GalleryTypeFragment galleryTypeFragment);

    void inject(LoginFragment loginFragment);

    void inject(FriendFragment friendFragment);

    void inject(FriendsGalleryFragment friendsGalleryFragment);

    void inject(ImageFragment imageFragment);

    void inject(ImageFromFriendFragment imageFromFriendFragment);

    void inject(FilterFragment filterFragment);
}
