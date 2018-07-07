//package net.yak.rekolink;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.SocketException;
//
//import android.content.Context;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//import android.util.Log;
//
//public class Player implements Runnable {
//    private Config config;
//    private Context context;
//
//    public PlayerStepMachine machine;
//    private Thread machineThread;
//
//    class PlayerStepMachine extends StepMachine {
//
//        public PlayerStepMachine(Context context, Step[] steps) {
//            super(context, steps, "[Player]");
//        }
//
//        void waitOrWhatever() {
//            if (machine.getLevel() == MainActivity.FOREGROUND_LEVEL) {
//                Player.this.whateverPlayer();
//            } else {
//                super.waitOrWhatever();
//            }
//        }
//    }
//
//    private Step[] steps = new Step[]{
//            new Step(MainActivity.PERMISSION_LEVEL, "Permission",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//            new Step(MainActivity.DNS_LEVEL, "DnsLookup",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//            new Step(MainActivity.BACKGROUND_LEVEL, "Player",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return startPlayer();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return stopPlayer();
//                        }
//                    }),
//            new Step(MainActivity.FOREGROUND_LEVEL, "Recorder",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//            new Step(MainActivity.TALK_LEVEL, "PTT",
//                    new Complainer.Starter() {
//                        public String run() {
//                            return nop();
//                        }
//                    },
//                    new Complainer.Stopper() {
//                        public String run() {
//                            return nop();
//                        }
//                    }),
//    };
//
//    public void run() {
//        machineThread = new Thread(machine);
//        machineThread.start();
//    }
//
//    String nop() {
//        return "";
//    }
//
//    private int progress = 0;
//
//    public Player(Config cf, Context cx) {
//        config = cf;
//        context = cx;
//        machine = new PlayerStepMachine(context, steps);
//    }
//
//    public void whateverPlayer() {
//    }
//
//
//    private synchronized void incrementProgress() {
//        progress++;
//    }
//
//    public synchronized int getProgress() {
//        return progress;
//    }
//    int getLevel() {
//        return machine.getLevel();
//    }
//}
