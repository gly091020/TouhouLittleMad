package com.gly091020.touhouLittleMad;

public enum MoodLevelType {
    GOOD,NORMAL,BAD,MAD;
    public static MoodLevelType getType(int mood){
        // 女仆心情等级
        if(mood >= 180){
            return MAD;
        } else if (mood >= 120) {
            return BAD;
        } else if (mood >= 60) {
            return NORMAL;
        }
        return GOOD;
    }

    public float getAddMagnification(){
        switch (this){
            case GOOD -> {return 1;}
            case NORMAL -> {return 1.5f;}
            case BAD -> {return 2;}
            default -> {return 0;}
        }
    }

    public float getSubMagnification(){
        switch (this){
            case NORMAL -> {return 0.75f;}
            case BAD -> {return 0.5f;}
            case MAD -> {return 0f;}
            default -> {return 1f;}
        }
    }

    public String getDebugText(){
        switch (this){
            case GOOD -> {return "开心";}
            case NORMAL -> {return "正常";}
            case BAD -> {return "伤心";}
            case MAD -> {return "可以换人了";}
            default -> {return "未知";}
        }
    }

    public int getAttackSpeed(){
        switch (this){
            case GOOD -> {
                return 8;
            }
            case BAD, MAD -> {
                return 10;
            }
            default -> {
                return 4;
            }
        }
    }
}
