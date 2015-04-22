package com.zncm.lovetuer.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.alibaba.fastjson.JSON;
import com.zncm.component.request.ReqService;
import com.zncm.component.request.ServiceParams;
import com.zncm.component.request.ServiceRequester;
import com.zncm.lovetuer.R;
import com.zncm.lovetuer.data.EnumData;
import com.zncm.lovetuer.data.base.NoteBookData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.utils.StrUtil;
import com.zncm.utils.exception.CheckedExceptionHandler;
import com.zncm.utils.sp.StatedPerference;

import java.util.List;

public class XUtil {
    public static void loginoutDo() {
        StatedPerference.setNoteBookData("");
        StatedPerference.setOauthToke("");
        SharedApplication.getInstance().setTokeData(null);
        SharedApplication.getInstance().setNoteBookDatas(null);
    }


    //日记本列表
    public static void getNoteBook() {
        try {
            ServiceParams params = new ServiceParams();
            ReqService.getDataFromServer(GlobalConstants.BASE_API_URL + "notebook/user/" + SharedApplication.getInstance().getTokeData().getTuer_uid(), params,
                    new ServiceRequester() {

                        @Override
                        public void onResult(String resultEx) {
                            if (StrUtil.notEmptyOrNull(resultEx)) {
                                List<NoteBookData> noteBookDatas = JSON.parseArray(JSON.parseObject(resultEx).getString("data"), NoteBookData.class);
                                SharedApplication.getInstance().setNoteBookDatas(noteBookDatas);
                                StatedPerference.setNoteBookData(noteBookDatas.toString());
                            }
                        }
                    }
            );
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }

    }


    public abstract static class TwoEtAlertDialogFragment extends SherlockDialogFragment {
        public String title;
        public String positive;
        public String negative;

        public TwoEtAlertDialogFragment(String title) {
            this.title = title;
            this.positive = "确定";
            this.negative = "取消";
        }

        protected TwoEtAlertDialogFragment(String title, String positive, String negative) {
            this.title = title;
            this.positive = positive;
            this.negative = negative;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.view_edit, null);
            final EditText editText = (EditText) view.findViewById(R.id.etInput);
            return new AlertDialog.Builder(getActivity()).setTitle(title).setView(view)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doPositiveClick(editText.getText().toString());
                        }
                    }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doNegativeClick();
                        }
                    }).create();
        }

        public abstract void doPositiveClick(String content);

        public abstract void doNegativeClick();

    }


    public abstract static class TwoAlertDialogFragment extends SherlockDialogFragment {
        public String title;
        public String positive;
        public String negative;

        public TwoAlertDialogFragment(String title) {
            this.title = title;
            this.positive = "确定";
            this.negative = "取消";
        }

        public TwoAlertDialogFragment(int icon, String title) {
            this.title = title;
            this.positive = "确定";
            this.negative = "取消";
        }

        public TwoAlertDialogFragment(String title, String positive, String negative) {
            this.title = title;
            this.positive = positive;
            this.negative = negative;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity()).setTitle(title)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doPositiveClick();
                        }
                    }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doNegativeClick();
                        }
                    }).create();
        }

        public abstract void doPositiveClick();

        public abstract void doNegativeClick();

    }

    public abstract static class ContextMenuDialog extends SherlockDialogFragment {
        public boolean bEidt;
        public boolean bDel;
        public boolean bCopy;


        public ContextMenuDialog(boolean bEidt, boolean bDel, boolean bCopy) {
            this.bEidt = bEidt;
            this.bDel = bDel;
            this.bCopy = bCopy;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.view_context_menu, null);
            final TableRow trEdit = (TableRow) view.findViewById(R.id.trEdit);
            final TableRow trDel = (TableRow) view.findViewById(R.id.trDel);
            final TableRow trCopy = (TableRow) view.findViewById(R.id.trCopy);

            if (bEidt) {
                trEdit.setVisibility(View.VISIBLE);
            } else {
                trEdit.setVisibility(View.GONE);
            }
            if (bDel) {
                trDel.setVisibility(View.VISIBLE);
            } else {
                trDel.setVisibility(View.GONE);
            }
            if (bCopy) {
                trCopy.setVisibility(View.VISIBLE);
            } else {
                trCopy.setVisibility(View.GONE);
            }

            trEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    doClick(EnumData.ContextMenuEnum.EDIT.getValue());
                }
            });
            trDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    doClick(EnumData.ContextMenuEnum.DEL.getValue());
                }
            });
            trCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    doClick(EnumData.ContextMenuEnum.COPY.getValue());
                }
            });
            return new AlertDialog.Builder(getActivity()).setView(view).create();

        }

        protected abstract void doClick(int type);


    }

//    public abstract static class CommentDialog extends SherlockDialogFragment {
//        public boolean bAuthor;
//
//
//        public CommentDialog(boolean bAuthor) {
//            this.bAuthor = bAuthor;
//        }
//
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.view_context_menu, null);
//            final TableRow trEdit = (TableRow) view.findViewById(R.id.trEdit);
//            final TableRow trDel = (TableRow) view.findViewById(R.id.trDel);
//            final TableRow trCopy = (TableRow) view.findViewById(R.id.trCopy);
//
//            if (bAuthor) {
//                trEdit.setVisibility(View.GONE);
//                trDel.setVisibility(View.VISIBLE);
//                trCopy.setVisibility(View.VISIBLE);
//            } else {
//                trEdit.setVisibility(View.GONE);
//                trDel.setVisibility(View.GONE);
//                trCopy.setVisibility(View.VISIBLE);
//            }
//
//            trEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                    doClick(EnumData.ContextMenuEnum.EDIT.getValue());
//                }
//            });
//            trDel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                    doClick(EnumData.ContextMenuEnum.DEL.getValue());
//                }
//            });
//            trCopy.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                    doClick(EnumData.ContextMenuEnum.COPY.getValue());
//                }
//            });
//            return new AlertDialog.Builder(getActivity()).setView(view).create();
//
//        }
//
//        protected abstract void doClick(int type);
//
//
//    }


}
