/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sejda.model.repaginate;

import org.sejda.common.FriendlyNamed;

public enum Repagination implements FriendlyNamed {
    /**
     * This repagination is useful in scenarios such as two page layout scans from unstapled booklets
     * (staples removed from the booklet and then the double pages scanned front/back)
     *
     * First scanned page contains the last and first covers (hence the name), second scanned page contains second and before-last page, and so on.
     * Example for a 10 pages booklet scanned in this manner: (10,1) (2,9) (8,3) (4,7) (6,5)
     *
     * Splitting the two-page layout document down the middle would result in: 10,1,2,9,8,3,4,7,6,5
     * Applying the last-first repagination in this case would order the pages as a reader would expect them: 1,2,3,4,5,6,7,8,9,10
     */
    LAST_FIRST("last-first"),
    NONE("none");

    private String displayName;

    private Repagination(String displayName) {
        this.displayName = displayName;
    }

    public String getFriendlyName() {
        return displayName;
    }
}

