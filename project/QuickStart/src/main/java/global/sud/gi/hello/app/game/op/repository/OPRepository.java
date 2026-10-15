package global.sud.gi.hello.app.game.op.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.listener.SUDOPGameListCallback;
import global.sud.op.runtime.core.model.SUDOPGameListItem;
import global.sud.op.runtime.core.model.SUDOPGameListParams;
import global.sud.op.runtime.core.model.SUDOPGameListResult;

public class OPRepository {

    static final int PAGE_SIZE = 20;
    private static final int MAX_PAGE_COUNT = 1000;

    private final GameListGateway gateway;
    private int generation;

    public OPRepository() {
        this(new SUDOPGameListGateway());
    }

    OPRepository(GameListGateway gateway) {
        this.gateway = gateway;
    }

    public void loadAll(String selectionId, String language, Callback callback) {
        int requestGeneration = ++generation;
        LinkedHashMap<String, SUDOPGameListItem> games = new LinkedHashMap<>();
        loadPage(normalizeSelectionId(selectionId), language, 0, 0, games,
                requestGeneration, callback);
    }

    public void cancel() {
        generation++;
    }

    static String normalizeSelectionId(String selectionId) {
        return selectionId == null ? "" : selectionId.trim();
    }

    private void loadPage(String selectionId,
                          String language,
                          int pageNo,
                          int fetchedCount,
                          LinkedHashMap<String, SUDOPGameListItem> games,
                          int requestGeneration,
                          Callback callback) {
        if (requestGeneration != generation) {
            return;
        }
        if (pageNo >= MAX_PAGE_COUNT) {
            callback.onFailure(-1, "Game list exceeded the maximum page count");
            return;
        }
        SUDOPGameListParams params = new SUDOPGameListParams();
        params.pageNo = pageNo;
        params.pageSize = PAGE_SIZE;
        params.language = language;
        gateway.load(selectionId, params, new SUDOPGameListCallback() {
            @Override
            public void onSuccess(SUDOPGameListResult result) {
                if (requestGeneration != generation) {
                    return;
                }
                if (result == null) {
                    callback.onFailure(-1, "Game list response is empty");
                    return;
                }
                if (result.pageNo != pageNo) {
                    callback.onFailure(-1, "Game list page did not advance");
                    return;
                }
                List<SUDOPGameListItem> records = result.records;
                int currentCount = records == null ? 0 : records.size();
                if (records != null) {
                    for (SUDOPGameListItem item : records) {
                        if (item != null && !isEmpty(item.gameId)) {
                            games.putIfAbsent(item.gameId, item);
                        }
                    }
                }
                int nextFetchedCount = fetchedCount + currentCount;
                if (currentCount == 0 || nextFetchedCount >= result.total) {
                    callback.onSuccess(new ArrayList<>(games.values()));
                    return;
                }
                loadPage(selectionId, language, pageNo + 1, nextFetchedCount,
                        games, requestGeneration, callback);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                if (requestGeneration == generation) {
                    callback.onFailure(retCode, retMsg);
                }
            }
        });
    }

    interface GameListGateway {
        void load(String selectionId,
                  SUDOPGameListParams params,
                  SUDOPGameListCallback callback);
    }

    private static class SUDOPGameListGateway implements GameListGateway {
        @Override
        public void load(String selectionId,
                         SUDOPGameListParams params,
                         SUDOPGameListCallback callback) {
            if (isEmpty(selectionId)) {
                SUDOP.getGameList(params, callback);
            } else {
                SUDOP.getGameList(selectionId, params, callback);
            }
        }
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public interface Callback {
        void onSuccess(List<SUDOPGameListItem> games);

        void onFailure(int retCode, String retMsg);
    }
}
