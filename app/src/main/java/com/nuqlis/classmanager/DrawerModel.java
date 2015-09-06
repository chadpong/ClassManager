package com.nuqlis.classmanager;

/**
 * Created by Chadpong on 5/9/2558.
 */
public class DrawerModel {

        private int icon;
        private String title;

        public DrawerModel(int icon, String title) {
            super();
            this.icon = icon;
            this.title = title;
        }
        public int GetIconID(){
            return this.icon;
        }

        public String GetTitle() {
            return this.title;
        }

}
