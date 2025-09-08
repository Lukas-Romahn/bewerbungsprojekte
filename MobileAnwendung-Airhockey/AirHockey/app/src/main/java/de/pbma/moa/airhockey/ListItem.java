package de.pbma.moa.airhockey;


import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.database.Game;
import de.pbma.moa.database.Tournament;

public class ListItem {
        private String turnierId;
        private String name;
        private String state;

        public ListItem(String name, String tid,String state) {
            this.name = name;
            this.turnierId = tid;
            this.state=state;

        }

        public String getName() {
            return name;
        }

        public String getTurnierId() {
            return turnierId;
        }
        public String getState(){
            return state;
        }





    }


